package sk.fei.beskydky.cryollet.ui.login.pin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.hanks.passcodeview.PasscodeView
import com.hanks.passcodeview.PasscodeView.PasscodeViewListener
import sk.fei.beskydky.cryollet.R
import sk.fei.beskydky.cryollet.databinding.FragmentPinCodeBinding

class PinCodeFragment : Fragment() {

    private lateinit var binding: FragmentPinCodeBinding
    private lateinit var viewModel: PinCodeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pin_code, container, false)
        viewModel = ViewModelProvider(this)[PinCodeViewModel::class.java]

        viewModel.eventPinFails.observe(viewLifecycleOwner, Observer {
            if (it){
                Toast.makeText(requireContext(), "Password is wrong!", Toast.LENGTH_SHORT).show()
                viewModel.onPinFailsFinished()
            }
        })

        viewModel.eventPinSucceed.observe(viewLifecycleOwner, Observer {
            if(it){
                binding.root.findNavController().navigate(PinCodeFragmentDirections.actionPinCodeFragmentToKeyLoginFragment())
                viewModel.onPinSucceedFinished()
            }
        })

        setUpPinView(binding, viewModel)
        return binding.root
    }

    private fun setUpPinView(binding: FragmentPinCodeBinding, viewModel: PinCodeViewModel){
        var type = PasscodeView.PasscodeViewType.TYPE_CHECK_PASSCODE
        var headerText = getString(R.string.insert_pin)


        if(viewModel.setUpPin){
            type = PasscodeView.PasscodeViewType.TYPE_SET_PASSCODE
            headerText = getString(R.string.set_up_pin)
        }
        else{
            binding.passcodeView.localPasscode = viewModel.pinCode
        }

        binding.passcodeView.firstInputTip = headerText
        binding.passcodeView.passcodeType = type

        binding.passcodeView.listener = object : PasscodeViewListener {
            override fun onFail() {
                viewModel.onPinFails()
            }

            override fun onSuccess(number: String?) {
                viewModel.onPinSucceed(number)
            }
        }
    }
}