package mb.safeEat.components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import mb.safeEat.R
import mb.safeEat.extensions.Alertable

data class OrderDetailParams(
    val status: OrderStatus,
    val restaurant: String,
    val date: String,
)

class OrderDetailFragment(
    private val navigation: NavigationListener, private val params: OrderDetailParams
) : Fragment(), Alertable {
    private lateinit var items: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_order_detail, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initHeader(view)
        initAdapter(view)
        initScreenEvents(view)
        loadInitialData()
    }

    private fun initHeader(view: View) {
        val title = view.findViewById<TextView>(R.id.header_title)
        val backButton = view.findViewById<MaterialCardView>(R.id.header_back_button)
        title.text = resources.getString(R.string.t_order)
        backButton.setOnClickListener { navigation.onBackPressed() }
    }

    private fun initAdapter(view: View) {
        items = view.findViewById(R.id.order_detail_items)
        items.layoutManager = LinearLayoutManager(view.context)
        items.adapter = OrderDetailAdapter()
    }

    private fun initScreenEvents(view: View) {
        val image = view.findViewById<ImageView>(R.id.order_detail_restaurant_image)
        val restaurant = view.findViewById<TextView>(R.id.order_detail_restaurant_name)
        val date = view.findViewById<TextView>(R.id.order_detail_date)
        val status = view.findViewById<TextView>(R.id.order_detail_status)
        val progressBar = view.findViewById<ProgressBar>(R.id.order_detail_progress_bar)
        val buttonFeedback = view.findViewById<Button>(R.id.order_detail_button_feedback)

        image.setImageResource(R.drawable.restaurant)
        restaurant.text = params.restaurant
        date.text = params.date
        status.text = params.status.toResourceString(view.context)
        progressBar.progressDrawable.setTint(
            ContextCompat.getColor(view.context, params.status.toResourceColor())
        )
        progressBar.progressDrawable.setTintMode(PorterDuff.Mode.DARKEN)
        progressBar.progress = params.status.toProgress()
        if (params.status == OrderStatus.DELIVERED) {
            buttonFeedback.setOnClickListener { navigation.navigateTo(FeedbackFragment(navigation)) }
            buttonFeedback.visibility = View.VISIBLE
        } else {
            buttonFeedback.visibility = View.GONE
        }
    }

    private fun loadInitialData() {
        // TODO: load data from API
        (items.adapter as OrderDetailAdapter).loadInitialData(createList())
    }

    private fun createList(): ArrayList<OrderItem> {
        return arrayListOf(
            OrderItem(10, "Hot-dog combo", "€10,00"),
            OrderItem(10, "Hot-dog combo", "€10,00"),
            OrderItem(10, "Hot-dog combo", "€10,00"),
        )
    }
}

class OrderDetailAdapter : RecyclerView.Adapter<OrderDetailAdapter.ViewHolder>() {
    private var data = ArrayList<OrderItem>()

    @SuppressLint("NotifyDataSetChanged")
    fun loadInitialData(newData: ArrayList<OrderItem>) {
        data = newData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_order_detail, parent, false)
    )

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(data[position])

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val quantity = itemView.findViewById<TextView>(R.id.order_detail_item_quantity)
        private val product = itemView.findViewById<TextView>(R.id.order_detail_item_product)
        private val price = itemView.findViewById<TextView>(R.id.order_detail_item_price)

        @SuppressLint("SetTextI18n")
        fun bind(item: OrderItem) {
            quantity.text =
                item.quantity.toString() + itemView.resources.getString(R.string.r_x_times)
            product.text = item.product
            price.text = item.price
        }
    }
}

data class OrderItem(
    val quantity: Int,
    val product: String,
    val price: String,
)

enum class OrderStatus {
    PREPARING, TRANSPORTING, DELIVERED;

    fun toResourceString(context: Context): String = when (this) {
        DELIVERED -> context.resources.getString(R.string.t_delivered)
        TRANSPORTING -> context.resources.getString(R.string.t_transporting)
        PREPARING -> context.resources.getString(R.string.t_preparing)
    }

    @ColorRes
    fun toResourceColor(): Int = when (this) {
        PREPARING -> R.color.orange_500
        TRANSPORTING -> R.color.orange_500
        DELIVERED -> R.color.green_500
    }

    fun toProgress(): Int = when (this) {
        PREPARING -> 33
        TRANSPORTING -> 66
        DELIVERED -> 100
    }

    //override fun toString() : String {
    //    return
    //}
}
