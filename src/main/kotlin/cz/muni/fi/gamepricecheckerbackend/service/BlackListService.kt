package cz.muni.fi.gamepricecheckerbackend.service

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

@Service
class BlackListService {

    private val blacklist = ConcurrentHashMap<String, Instant>()
    private val lock = ReentrantLock()

    fun addToBlackList(token: String) {
        lock.withLock {
            blacklist[token] = Instant.now().plus(Duration.ofHours(1))
        }
    }

    fun isBlackListed(token: String): Boolean {
        lock.withLock {
            return blacklist.containsKey(token)
        }
    }

    @Scheduled(cron = "@hourly")
    fun removeExpiredTokens() {
        lock.withLock {
            val now = Instant.now()
            blacklist.entries.removeIf { it.value.isBefore(now) }
        }
    }
}
