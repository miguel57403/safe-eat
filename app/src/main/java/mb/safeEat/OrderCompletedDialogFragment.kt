package mb.safeEat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import androidx.fragment.app.DialogFragment

class OrderCompletedDialogFragment : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_order_completed, container)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT, // width
            WindowManager.LayoutParams.WRAP_CONTENT // height
        )
        val okButton = dialog!!.window!!.findViewById<Button>(R.id.order_completed_ok_button)
        okButton.setOnClickListener { dialog!!.dismiss() }
    }
}