package com.vinsol.meetingscheduler.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vinsol.meetingscheduler.adapter.MeetingAdapter
import com.vinsol.meetingscheduler.viewmodel.MeetingsViewModel
import com.vinsol.meetingscheduler.R
import com.vinsol.meetingscheduler.di.ViewModelFactory
import com.vinsol.meetingscheduler.extensions.formatDate
import kotlinx.android.synthetic.main.fragment_meeting_list.*
import java.util.*
import javax.inject.Inject
import android.content.res.Configuration

class MeetingListFragment : BaseFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: MeetingsViewModel
    private lateinit var adapter: MeetingAdapter


    companion object {
        fun newInstance(): MeetingListFragment {
            return MeetingListFragment()
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MeetingsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_meeting_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        adapter = MeetingAdapter()
        rv_meetings.adapter = adapter
        rv_meetings.layoutManager = LinearLayoutManager(context)
        tv_previous.setOnClickListener {
            progress_bar.visibility = View.VISIBLE
            viewModel.calendar.add(Calendar.DAY_OF_YEAR, -1)
            viewModel.loadMeetings()
        }
        tv_next.setOnClickListener {
            progress_bar.visibility = View.VISIBLE
            viewModel.calendar.add(Calendar.DAY_OF_YEAR, 1)
            viewModel.loadMeetings()
        }
        btn_schedule_meeting.setOnClickListener {
            val fragment = ScheduleMeetingFragment.newInstance()
            fragmentManager?.beginTransaction()
                ?.replace(R.id.fragment_container, fragment, null)
                ?.addToBackStack("ScheduleMeeting")
                ?.commit()
        }

        viewModel.meetingsLiveData.observe(this, Observer { meetings ->
            meetings?.let { adapter.meetings = it }
            val orientation = resources.configuration.orientation
            val titleFormat = if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                "EEEE, dd-MM-yyyy"
            } else {
                "dd-MM-yyyy"
            }

            tv_schedule_date.text = viewModel.calendar.time.formatDate(titleFormat)
            progress_bar.visibility = View.GONE
        })
    }
}