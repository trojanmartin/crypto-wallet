package sk.fei.beskydky.cryollet.home.sendpayment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import org.stellar.sdk.responses.AccountResponse
import sk.fei.beskydky.cryollet.R
import sk.fei.beskydky.cryollet.contacts.ContactsViewModel
import sk.fei.beskydky.cryollet.contacts.ContactsViewModelFactory
import sk.fei.beskydky.cryollet.database.appDatabase.AppDatabase
import sk.fei.beskydky.cryollet.database.repository.BalanceRepository
import sk.fei.beskydky.cryollet.database.repository.ContactsRepository
import sk.fei.beskydky.cryollet.databinding.ContactsFragmentBinding
import sk.fei.beskydky.cryollet.databinding.FragmentSendPaymentBinding
import sk.fei.beskydky.cryollet.home.qrcode.QrCodeFragmentArgs
import sk.fei.beskydky.cryollet.setHideKeyboardOnClick
import sk.fei.beskydky.cryollet.stellar.StellarHandler

class SendPaymentFragment : Fragment() {

    private lateinit var viewModel: SendPaymentViewModel
    private lateinit var binding : FragmentSendPaymentBinding
    private lateinit var currencyList : ArrayList<String>

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_send_payment, container, false)

        val sendPaymentButton = binding.payButton


        val application = requireNotNull(this.activity).application
        val databaseDataSource = AppDatabase.getInstance(application).appDatabaseDao
        val stellarDataSource = StellarHandler.getInstance(application)
        val viewModelFactory = SendPaymentViewModelFactory(BalanceRepository(databaseDataSource, stellarDataSource))

        viewModel = ViewModelProvider(this, viewModelFactory)[SendPaymentViewModel::class.java]

        viewModel.currencyList.observe(viewLifecycleOwner, Observer {
            binding.currencyAutocomplete.setAdapter(ArrayAdapter(requireContext(), R.layout.currency_dropdown_item, it))
        })

        viewModel.contactList.observe
        //binding.sendPaymentContact.setAdapter(ArrayAdapter(requireContext(), R.layout.currency_dropdown_item, contacts))


        binding.sendPaymentAmount.doAfterTextChanged {
            if (it?.length == 2 && "00" == it.toString()) {
                binding.sendPaymentAmount.text.clear()
            }
        }

        val qrResponseData = SendPaymentFragmentArgs.fromBundle(requireArguments())
        if(qrResponseData.qrCodeResult != "") {
            val resolvedResult : List<String> = qrResponseData.qrCodeResult.split(',')
            viewModel.amount.value = resolvedResult[0]
            viewModel.currency.value = resolvedResult[1]
            viewModel.walletKey.value = resolvedResult[2]
        }

        viewModel.eventScanQRCode.observe(viewLifecycleOwner, Observer {
            if (it) {
                findNavController()
                        .navigate(
                                SendPaymentFragmentDirections
                                        .actionSendPaymentFragmentToQrCodeScannerFragment())
            }
        })

//        viewModel.currency.observe(viewLifecycleOwner, Observer {
//            viewModel.searchCurrency(it)
//        })

        viewModel.contactName.observe(viewLifecycleOwner, Observer {
            viewModel.searchContacts(it)
        })

        viewModel.eventPaymentCompleted.observe(viewLifecycleOwner, Observer {
            findNavController().navigate(SendPaymentFragmentDirections.actionSendPaymentFragmentToPaymentResultFragment())
        })

        viewModel.formState.observe(viewLifecycleOwner, Observer {
            sendPaymentButton.isEnabled = it.isValid()
        })

        viewModel.formObserver.observe(viewLifecycleOwner, {})

        binding.root?.setHideKeyboardOnClick(this)

        binding.viewModel = viewModel
        return binding.root

    }

}