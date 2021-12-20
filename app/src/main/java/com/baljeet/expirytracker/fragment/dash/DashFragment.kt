package com.baljeet.expirytracker.fragment.dash

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.data.Category
import com.baljeet.expirytracker.data.Tracker
import com.baljeet.expirytracker.data.relations.TrackerAndProduct
import com.baljeet.expirytracker.data.viewmodels.CategoryViewModel
import com.baljeet.expirytracker.data.viewmodels.TrackerViewModel
import com.baljeet.expirytracker.databinding.FragmentDashBinding
import com.baljeet.expirytracker.interfaces.UpdateTrackerListener
import com.baljeet.expirytracker.listAdapters.TrackerDiffAdapter
import com.baljeet.expirytracker.util.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.Job
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.time.Month
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class DashFragment : Fragment(), UpdateTrackerListener {

    private lateinit var disposable: Disposable

    private lateinit var trackerAdapter : TrackerDiffAdapter
    private val categoryVM: CategoryViewModel by viewModels()
    private val trackerVm: TrackerViewModel by activityViewModels()

    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent
    private val calendar = Calendar.getInstance()

    private val messages = ArrayList<String>()
    private val categories = ArrayList<Category>()

    private lateinit var bind: FragmentDashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        disposable = Observable.interval(
            3000, 3000,
            TimeUnit.MILLISECONDS
        )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::setStatus, this::onError)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentDashBinding.inflate(inflater, container, false)
        activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility =
            View.VISIBLE
        SharedPref.init(requireContext())
        setTimeAndGreetings()

        bind.addProductFab.setOnClickListener {
            Navigation.findNavController(requireView())
                .navigate(R.id.action_dashFragment_to_addProduct)
        }
        bind.addProductButton.setOnClickListener {
            Navigation.findNavController(requireView())
                .navigate(DashFragmentDirections.actionDashFragmentToAddProduct())
        }

        bind.trackerRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        trackerAdapter = TrackerDiffAdapter(requireContext(),this)
        bind.trackerRecyclerView.adapter = trackerAdapter

        trackerVm.filteredTrackers.let {
            it.observe(viewLifecycleOwner, { its ->
                if (trackerVm.noTrackerIsActive) {
                    noItemView()
                    disposable.dispose()
                } else {
                    setDashList(its)
                }
            })
        }

        bind.trackerRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                bind.addProductFab.apply {
                    //scroll down
                    if (dy > 30 && isExtended) {
                        shrink()
                    }
                    //reached the top of list
                    if (!recyclerView.canScrollVertically(-1)) {
                        extend()
                    }
                }

            }
        })

        bind.statusChip.apply {
            setOnClickListener {
                if (bind.statusLayout.isGone) {
                    bind.statusLayout.visibility = View.VISIBLE
                    bind.categoryLayout.visibility = View.GONE
                    chipBackgroundColor =
                        ColorStateList.valueOf(requireContext().getColor(R.color.text_dialog_color))

                    setTextColor(requireContext().getColor(R.color.main_background))

                    bind.productCategoryChip.chipBackgroundColor =
                        ColorStateList.valueOf(requireContext().getColor(R.color.window_top_bar))
                    bind.productCategoryChip.setTextColor(requireContext().getColor(R.color.always_white))

                } else {
                    chipBackgroundColor =
                        ColorStateList.valueOf(requireContext().getColor(R.color.window_top_bar))
                    setTextColor(requireContext().getColor(R.color.always_white))
                    bind.statusLayout.visibility = View.GONE
                }
            }


            bind.productCategoryChip.apply {
                setOnClickListener {
                    if (bind.categoryLayout.isGone) {
                        bind.statusLayout.visibility = View.GONE
                        bind.categoryLayout.visibility = View.VISIBLE
                        chipBackgroundColor =
                            ColorStateList.valueOf(requireContext().getColor(R.color.text_dialog_color))

                        setTextColor(requireContext().getColor(R.color.main_background))

                        bind.statusChip.chipBackgroundColor =
                            ColorStateList.valueOf(requireContext().getColor(R.color.window_top_bar))
                        bind.statusChip.setTextColor(requireContext().getColor(R.color.always_white))
                    } else {
                        chipBackgroundColor =
                            ColorStateList.valueOf(requireContext().getColor(R.color.window_top_bar))
                        setTextColor(requireContext().getColor(R.color.always_white))
                        bind.categoryLayout.visibility = View.GONE
                    }
                }
            }

            bind.statusChoiceList.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    bind.choiceAll.id -> {
                        text = Status.ALL.status
                        trackerVm.statusFilter.postValue(Constants.PRODUCT_STATUS_ALL)
                    }
                    bind.choiceExpired.id -> {
                        text = Status.EXPIRED.status
                        trackerVm.statusFilter.postValue(Constants.PRODUCT_STATUS_EXPIRED)
                    }
                    bind.choiceExpiring.id -> {
                        text = Status.EXPIRING.status
                        trackerVm.statusFilter .postValue(Constants.PRODUCT_STATUS_EXPIRING)
                    }
                    bind.choiceFresh.id -> {
                        text = Status.FRESH.status
                        trackerVm.statusFilter.postValue(Constants.PRODUCT_STATUS_FRESH)
                    }
                    else -> {
                        text = Status.ALL.status
                        trackerVm.statusFilter.postValue(Constants.PRODUCT_STATUS_ALL)
                    }
                }
                bind.statusLayout.visibility = View.GONE
                chipBackgroundColor =
                    ColorStateList.valueOf(requireContext().getColor(R.color.window_top_bar))
                setTextColor(requireContext().getColor(R.color.always_white))
            }

            createNotificationChannel()

            if (!SharedPref.isAlertEnabled) {
                val time = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                setReminderForProducts(time.hour, time.minute.plus(5))
                SharedPref.isAlertEnabled = true
            }

            bind.greetingText.setOnClickListener {
                if (SharedPref.isAlertEnabled) {
                    alarmManager =
                        requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    val intent = Intent(requireContext(), NotificationReceiver::class.java)
                    pendingIntent = PendingIntent.getBroadcast(
                        requireContext(), 0, intent,
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    )
                    alarmManager.cancel(pendingIntent)
                    SharedPref.isAlertEnabled = false
                    Toast.makeText(requireContext(), "Alerts disabled", Toast.LENGTH_SHORT).show()
                } else {
                    val time =
                        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                    setReminderForProducts(time.hour, time.minute.plus(5))
                    SharedPref.isAlertEnabled = true
                    Toast.makeText(requireContext(), "Alerts enabled", Toast.LENGTH_SHORT).show()
                }
            }
            getCategoriesChips()
            getStatus()
        }
        trackerVm.favouriteFilter.observe(viewLifecycleOwner, { filter->
            when(filter){
                Constants.SHOW_ALL ->{
                    bind.favouriteToggle.setImageDrawable(AppCompatResources.getDrawable(requireContext(),R.drawable.ic_star_half_32))
                }
                Constants.SHOW_ONLY_FAVOURITE->{
                    bind.favouriteToggle.setImageDrawable(AppCompatResources.getDrawable(requireContext(),R.drawable.ic_round_star_32))
                }
                Constants.SHOW_ONLY_NON_FAVOURITE->{
                    bind.favouriteToggle.setImageDrawable(AppCompatResources.getDrawable(requireContext(),R.drawable.ic_star_outline_32))
                }
            }
        })
        bind.favouriteToggle.apply {
            setOnClickListener {
                trackerVm.favouriteFilter.value?.let { filter ->
                    when (filter) {
                        Constants.SHOW_ALL -> {
                            trackerVm.favouriteFilter.postValue(Constants.SHOW_ONLY_FAVOURITE)
                        }
                        Constants.SHOW_ONLY_FAVOURITE -> {
                            trackerVm.favouriteFilter.postValue(Constants.SHOW_ONLY_NON_FAVOURITE)
                        }
                        else -> {
                            trackerVm.favouriteFilter.postValue(Constants.SHOW_ALL)
                        }
                    }
                }?: kotlin.run {
                    trackerVm.favouriteFilter.postValue(Constants.SHOW_ONLY_FAVOURITE)
                }
            }
        }
        return bind.root
    }

    private fun setDashList(list: List<TrackerAndProduct>) {
        if (list.isNullOrEmpty()) {
            Log.d("Log for - dash","no tracker view called")
            bind.noItemText.visibility = View.VISIBLE
            bind.trackerRecyclerView.visibility = View.GONE
            if (trackerVm.statusFilter.value == Constants.PRODUCT_STATUS_ALL) {
                bind.noItemText.text = resources.getString(
                    R.string.no_item_text1,
                    trackerVm.categoryFilter.value?.categoryName
                )
            } else {
                bind.noItemText.text = resources.getString(
                    R.string.no_item_text,
                    trackerVm.statusFilter.value?:Constants.PRODUCT_STATUS_ALL,
                    trackerVm.categoryFilter.value?.categoryName
                )
            }
        } else {
            Log.d("Log for - dash","tracker list showing")
            bind.trackerLayout.visibility = View.VISIBLE
            bind.trackerRecyclerView.visibility = View.VISIBLE
            bind.noItemText.visibility = View.GONE
            bind.noTrackerLayout.visibility = View.GONE
            bind.addProductFab.visibility = View.VISIBLE
            bind.imageForAnimation.visibility = View.VISIBLE
            bind.addProductButton.visibility = View.GONE

            trackerAdapter.submitList(list)

        }
    }

    private fun noItemView() {
        bind.noTrackerLayout.visibility = View.VISIBLE
        bind.trackerLayout.visibility = View.GONE
        bind.addProductFab.visibility = View.GONE
        bind.imageForAnimation.visibility = View.GONE
        bind.addProductButton.visibility = View.VISIBLE
        disposable.dispose()
    }

    private fun onError(throwable: Throwable) {
        Toast.makeText(
            requireContext(),
            "${throwable.message} error while syncing new updates",
            Toast.LENGTH_SHORT
        ).show()
        disposable.dispose()
    }

    private var messageNum = 0
    private fun setStatus(aLong: Long) {
        Log.d("Log for - ","$aLong")
        bind.imageForAnimation.apply {
            animate().scaleX(1.5f).scaleY(1.5f).alpha(0f).setDuration(1200)
                .withEndAction {
                    scaleX = 1f
                    scaleY = 1f
                    alpha = 1f
                }
        }
        if (messages.size > 0) {
            bind.statusText.apply {
                animate().alpha(0f).setDuration(1200)
                    .withEndAction {
                        alpha = 1f
                        text = messages[messageNum]
                    }
            }
            if (messageNum == messages.size - 1) {
                messageNum = 0
            } else {
                messageNum++
            }
        }
    }

    private fun getCategoriesChips() {
        trackerVm.categoryFilter.value?.let {
            bind.productCategoryChip.text = it.categoryName
        }
        categoryVM.readAllCategories?.let {
            it.observe(viewLifecycleOwner, { cats ->
                if (!cats.isNullOrEmpty()) {
                    bind.categoriesChoiceList.apply {
                        categories.clear()
                        categories.addAll(cats)
                        for (category in cats) {
                            val chip = Chip(
                                requireContext(),
                                null,
                                R.style.Widget_MaterialComponents_Chip_Choice
                            )
                            chip.text = category.categoryName
                            chip.id = category.categoryId
                            chip.isCheckable = true
                            chip.isClickable = true
                            chip.chipBackgroundColor =
                                ColorStateList.valueOf(requireContext().getColor(R.color.window_top_bar))
                            chip.setTextColor(requireContext().getColor(R.color.always_white))
                            chip.checkedIcon = AppCompatResources.getDrawable(
                                requireContext(),
                                R.drawable.check_circle_24
                            )
                            chip.isCheckedIconVisible = true
                            chip.checkedIconTint =
                                ColorStateList.valueOf(requireContext().getColor(R.color.always_white))
                            chip.chipMinHeight = 70f
                            chip.minWidth = 50
                            chip.textAlignment = View.TEXT_ALIGNMENT_CENTER
                            chip.isChecked =
                                trackerVm.categoryFilter.value?.let { cat->
                                    cat.categoryId == category.categoryId
                                } ?: false
                            addView(chip)
                        }
                    }
                }
            })
        }
        bind.categoriesChoiceList.setOnCheckedChangeListener { _, checkedId ->
            val category = categories.firstOrNull { c -> c.categoryId == checkedId }
            category?.let {
                bind.productCategoryChip.text = category.categoryName
                trackerVm.categoryFilter.postValue(category)
            } ?: kotlin.run {
                bind.productCategoryChip.text = resources.getString(R.string.products)
                trackerVm.categoryFilter.postValue(Category(0, "Products", 0))
            }
            bind.categoryLayout.visibility = View.GONE
            bind.productCategoryChip.chipBackgroundColor =
                ColorStateList.valueOf(requireContext().getColor(R.color.window_top_bar))
            bind.productCategoryChip.setTextColor(requireContext().getColor(R.color.always_white))
        }
    }
    private var getStatusJob: Job? = null

    private fun getStatus() {
        getStatusJob?.let {
            if (it.isActive) {
                it.cancel()
            }
        }
        getStatusJob = lifecycleScope.launchWhenCreated {
            messages.addAll(ProductStatus.getStatusMessage(requireContext()))
        }
    }

    private fun setReminderForProducts(hour: Int, minutes: Int) {

        calendar[Calendar.HOUR_OF_DAY] = hour
        calendar[Calendar.MINUTE] = minutes
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0

        alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), NotificationReceiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(
            requireContext(), 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
            AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent
        )
    }

    private fun createNotificationChannel() {
        val name = "ExpiryTrackerReminderBaljeet"
        val description = "Channel for ExpiryTracker Reminders"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel("expiryTrackerBaljeet", name, importance)
        channel.description = description
        val notificationManager = requireContext().getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }


    override fun onPause() {
        super.onPause()
        disposable.dispose()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }

    override fun onStop() {
        super.onStop()
        disposable.dispose()
    }

    override fun onResume() {
        super.onResume()
        if (disposable.isDisposed) {
            disposable = Observable.interval(
                3000, 3000,
                TimeUnit.MILLISECONDS
            )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setStatus, this::onError)
        }
    }

    private fun setTimeAndGreetings() {
        val dateTime = Clock.System.now().toLocalDateTime(Constants.TIMEZONE)
        bind.currentDate.text = resources.getString(
            R.string.date_var,
            Month.of(dateTime.monthNumber).name.substring(0, 3),
            dateTime.dayOfMonth,
            dateTime.year
        )
        // bind.currentTime.text = TimeConvertor.getTime(dateTime.hour,dateTime.minute,true)
        bind.greetingText.apply {
            when {
                dateTime.hour < 12 -> {
                    text = resources.getString(R.string.morning_greeting_text)
                }
                dateTime.hour in 12..15 -> {
                    text = resources.getString(R.string.afternoon_greeting_text)
                }
                dateTime.hour >= 16 -> {
                    text = resources.getString(R.string.evening_greeting_text)
                }
                else -> Unit
            }
        }
    }

    override fun updateTracker(updatedTracker: Tracker) {
        trackerVm.updateTracker(updatedTracker)
    }
}

