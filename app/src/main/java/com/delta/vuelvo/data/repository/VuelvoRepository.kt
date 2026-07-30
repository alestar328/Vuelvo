package com.delta.vuelvo.data.repository

import android.content.Context
import com.delta.vuelvo.BuildConfig
import com.delta.vuelvo.data.Reward
import com.delta.vuelvo.data.StampCard
import com.delta.vuelvo.data.VuelvoData
import com.delta.vuelvo.data.local.RewardDao
import com.delta.vuelvo.data.local.StampCardDao
import com.delta.vuelvo.data.local.entity.StampCardEntity
import com.delta.vuelvo.data.local.entity.VuelvoRewardEntity
import com.delta.vuelvo.data.mapper.toCommerceIcon
import com.delta.vuelvo.data.mapper.toEntity
import com.delta.vuelvo.data.mapper.toUi
import com.delta.vuelvo.domain.ScanResult
import com.delta.vuelvo.nfc.StampPayload
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Single source of truth for cards and rewards. Owns the seed-on-first-launch logic
 * and the stamp/redeem business rules described in the data model.
 */
@Singleton
class VuelvoRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cardDao: StampCardDao,
    private val rewardDao: RewardDao,
) {
    private val prefs by lazy { context.getSharedPreferences(PREFS, Context.MODE_PRIVATE) }

    val cards: Flow<List<StampCard>> = cardDao.observeAll().map { list -> list.map { it.toUi() } }
    val rewards: Flow<List<Reward>> = rewardDao.observeAll().map { list -> list.map { it.toUi() } }

    fun card(id: String): Flow<StampCard?> = cardDao.observeById(id).map { it?.toUi() }

    /**
     * Inserts the sample catalogue once, guarded by a SharedPreferences flag.
     * Only seeds in debug builds: testers and Play Store users start with an empty
     * app and only get cards by scanning a real NFC tag. The sample data is purely
     * a development convenience.
     */
    suspend fun seedIfNeeded() {
        if (!BuildConfig.DEBUG) return
        if (prefs.getBoolean(KEY_SEEDED, false)) return
        cardDao.insertAll(VuelvoData.cards.map { it.toEntity() })
        val now = System.currentTimeMillis()
        rewardDao.insertAll(
            VuelvoData.rewards.mapIndexed { i, r -> r.toEntity(createdAt = now - i * 1000L) },
        )
        prefs.edit().putBoolean(KEY_SEEDED, true).apply()
    }

    /**
     * Applies a stamp from a scanned tag / deep link. Creates the card if it is unknown,
     * otherwise adds a stamp to the existing one.
     *
     * An existing card also refreshes the tag-owned fields from the payload, so a comercio that
     * re-writes its tag (new name, new reward, new logo/cover) reaches customers who already hold
     * the card. Images are only overwritten when the tag actually carries one: older tags predate
     * `logo=`/`cover=`, and scanning one of those must not wipe the logo a newer tag installed.
     * Stamp count is card state, not tag state, so it is never touched here.
     */
    suspend fun applyStamp(payload: StampPayload): ScanResult {
        val existing = cardDao.findById(payload.id)
        val card = existing?.copy(
            name = payload.name,
            maxStamps = payload.max,
            reward = payload.reward,
            uuid = payload.uuid ?: existing.uuid,
            logoRef = payload.logoRef ?: existing.logoRef,
            coverRef = payload.coverRef ?: existing.coverRef,
        ) ?: StampCardEntity(
            id = payload.id,
            name = payload.name,
            category = "Comercio",
            symbolName = com.delta.vuelvo.data.CommerceIcon.COFFEE.name,
            tileHex = "#EDE7FB",
            inkHex = "#7B3CE6",
            stamps = 0,
            maxStamps = payload.max,
            reward = payload.reward,
            uuid = payload.uuid,
            logoRef = payload.logoRef,
            coverRef = payload.coverRef,
        )
        return addStamp(card)
    }

    /** Increments a card; on reaching [StampCardEntity.maxStamps] mints a reward and resets. */
    suspend fun addStamp(card: StampCardEntity): ScanResult {
        val next = card.stamps + 1
        val icon = card.symbolName.toCommerceIcon()
        return if (next >= card.maxStamps) {
            rewardDao.upsert(
                VuelvoRewardEntity(
                    id = UUID.randomUUID().toString(),
                    cardId = card.id,
                    name = card.reward,
                    commerce = card.name,
                    symbolName = card.symbolName,
                    tileHex = card.tileHex,
                    inkHex = card.inkHex,
                    isAvailable = true,
                    dateLabel = "Hoy",
                    createdAt = System.currentTimeMillis(),
                    logoRef = card.logoRef,
                ),
            )
            cardDao.upsert(card.copy(stamps = 0))
            ScanResult(
                gotReward = true,
                cardName = card.name,
                reward = card.reward,
                newCount = card.maxStamps,
                max = card.maxStamps,
                icon = icon,
                logoRef = card.logoRef,
            )
        } else {
            cardDao.upsert(card.copy(stamps = next))
            ScanResult(
                gotReward = false,
                cardName = card.name,
                reward = null,
                newCount = next,
                max = card.maxStamps,
                icon = icon,
                logoRef = card.logoRef,
            )
        }
    }

    /** Removes a card. Earned rewards stay in the rewards history. */
    suspend fun deleteCard(id: String) = cardDao.deleteById(id)

    /** Marks a reward as redeemed and resets its originating card's stamp count. */
    suspend fun confirmRedeem(reward: Reward) {
        rewardDao.findById(reward.id)?.let {
            rewardDao.upsert(it.copy(isAvailable = false, dateLabel = "Hoy"))
        }
        cardDao.findById(reward.cardId)?.let {
            cardDao.upsert(it.copy(stamps = 0))
        }
    }

    private companion object {
        const val PREFS = "vuelvo.prefs"
        const val KEY_SEEDED = "vuelvo.seedInserted"
    }
}
