package mb.safeEat.components

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.card.MaterialCardView
import mb.safeEat.R
import mb.safeEat.extensions.Alertable
import mb.safeEat.functions.suspendToLiveData
import mb.safeEat.services.api.api
import mb.safeEat.services.api.dto.UserDto

class AllergyEditFragment(private val navigation: NavigationListener) : AllergyListener, Fragment(), Alertable  {
    // TODO: Create a fragment for allergies buttons
    private lateinit var items: RecyclerView
    private var data = ArrayList<Allergy>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_allergy_edit, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initHeader(view)
        initialAdapter(view)
        loadInitialData()
        initScreenEvents(view)
    }

    private fun initHeader(view: View) {
        val title = view.findViewById<TextView>(R.id.header_title)
        val backButton = view.findViewById<MaterialCardView>(R.id.header_back_button)
        title.text = resources.getString(R.string.t_edit_allergies)
        backButton.setOnClickListener { navigation.onBackPressed() }
    }

    private fun initialAdapter(view: View) {
        items = view.findViewById(R.id.allergies_buttons)
        items.layoutManager = FlexboxLayoutManager(view.context).apply {
            flexWrap = FlexWrap.WRAP
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.CENTER
        }
        items.adapter = AllergyAdapter(this)
        items.visibility = View.VISIBLE
    }

    private fun loadInitialData() {
        suspendToLiveData { api.restrictions.findAll() }.observe(viewLifecycleOwner) { result ->
            result.fold(onSuccess = { restrictions ->
                val initialData = mapInitialData(restrictions)
                data = initialData
                (items.adapter as AllergyAdapter).loadInitialData(initialData)
            }, onFailure = {
                // TODO: Spread this error handling to other places
                alertThrowable(it)
                Log.d("Api Error", "$it")
            })
        }
    }

    private fun mapInitialData(restrictions: List<mb.safeEat.services.api.models.Restriction>): ArrayList<Allergy> {
        return restrictions.map { Allergy(it.id!!, it.name!!, false) }.toCollection(ArrayList())
    }

    private fun initScreenEvents(view: View) {
        val button = view.findViewById<Button>(R.id.allergy_submit)
        button.setOnClickListener {
            hideKeyboard(button)
            val selected = data.filter { it.selected }.map { it.id }
            val body = UserDto(restrictionIds = selected)
            suspendToLiveData { api.users.update(body) }.observe(viewLifecycleOwner) { result ->
                result.fold(onSuccess = {
                    navigation.onBackPressed()
                }, onFailure = {
                    alertThrowable(it)
                    Log.d("Api Error", "$it")
                })
            }
        }
    }

    override fun onClickAllergy(allergy: Allergy, position: Int) {
        data[position] = allergy.copy(selected = !allergy.selected)
        (items.adapter as AllergyAdapter).updateItem(position, data[position])
    }

    private fun hideKeyboard(button: View) {
        val inputMethodManager =
            ContextCompat.getSystemService(button.context, InputMethodManager::class.java)!!
        inputMethodManager.hideSoftInputFromWindow(button.windowToken, 0)
    }
}
