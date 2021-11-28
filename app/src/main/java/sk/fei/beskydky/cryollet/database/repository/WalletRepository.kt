package sk.fei.beskydky.cryollet.database.repository

import androidx.annotation.WorkerThread
import sk.fei.beskydky.cryollet.aesDecrypt
import sk.fei.beskydky.cryollet.aesEncrypt
import sk.fei.beskydky.cryollet.data.model.Wallet
import sk.fei.beskydky.cryollet.database.appDatabase.AppDatabaseDao
import sk.fei.beskydky.cryollet.stellar.StellarHandler

class WalletRepository(private val appDatabaseDao: AppDatabaseDao, private val stellarDataSource: StellarHandler) {


    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(wallet: Wallet) {
        appDatabaseDao.updateWallet(wallet)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun get(): Wallet? {
        return appDatabaseDao.getWallet()
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun createAndInsert(userId:Long, pin:String) {
        appDatabaseDao.clearAllWallets()
        var registeredKeyPair = stellarDataSource.createAccount()
        val hashedSecretKey = registeredKeyPair.secretSeed.toString().aesEncrypt(pin)
        appDatabaseDao.insertWallet(Wallet(userId = userId, accountId = registeredKeyPair.accountId, secretKey = hashedSecretKey , balance = 10000.0))
    }
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getSecretKey(pin:String) :String? {
        val wallet = get()
        return wallet?.secretKey?.aesDecrypt(pin)
    }


    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteAll() {
        appDatabaseDao.clearAllWallets()
    }


}