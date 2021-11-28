package sk.fei.beskydky.cryollet.home.sendpayment

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.stellar.sdk.KeyPair
import sk.fei.beskydky.cryollet.stellar.StellarHandler

data class User(val name: String)

class SendPaymentViewModel : ViewModel() {
    val user = MutableLiveData("")
    var userList: MutableLiveData<ArrayList<String>> = MutableLiveData()

    val contactName = MutableLiveData("")
    var contactList: MutableLiveData<ArrayList<String>> = MutableLiveData()

    //dump
    fun searchCurrency(name: String) {
        val list = ArrayList<String>()

        list.add("CZK - Czech Koruna")
        list.add("DKK - Danish Krone")
        list.add("DOP - Dominican Peso")
        list.add("EUR - Euro")
        list.add("ALL - Albanian Lek")
        list.add("AMD - Armenian Dram")
        list.add("AOA - Angolan Kwanza")
        list.add("ARS - Argentine Peso")
        list.add("PLN - Polish Zloty")
        list.add("PYG - Paraguayan Guarani")
        list.add("QAR - Qatari Riyal")
        list.add("RON - Romanian Leu")
        userList.value = list
    }

    // dump
    fun searchContacts(name: String) {
        val list = ArrayList<String>()

        list.add("Fero Pajta")
        list.add("Lukas Hajducak")
        list.add("Tanicka Smolarova")
        list.add("Bukvica")

        contactList.value = list
    }

    fun onClick() = viewModelScope.launch {
        val stellarHandler = StellarHandler()

        val source: KeyPair = KeyPair.fromSecretSeed("SCZWUQZX7AD7OENXXKNOHJXSOT2WAJOBRLVV7YNASLAMOECWTJPAC3WS")
        val destinationId: String = "GAWB5RG6F4X3SUBXYI3O3M4ZED6KFHMORIM5URKZI5BYRCJHGOO5XSLP"

        val response = stellarHandler.getPayments(source)
        Log.i("Stellar", "Success")
    }
}