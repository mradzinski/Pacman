package com.mradzinski.pacman

@Suppress("MemberVisibilityCanPrivate", "unused")
class Pacman(callGroups: List<CallGroup>, listener: OnCallsCompleteListener) {
    private val requestedCallGroups: MutableList<CallGroup> = mutableListOf()
    private val completedCallGroups: MutableList<CallGroup> = mutableListOf()
    private var onCallsCompleteListener: OnCallsCompleteListener

    interface OnCallsCompleteListener { fun onCallsCompleted() }

    init {
        requestedCallGroups.addAll(callGroups)
        onCallsCompleteListener = listener
    }

    /**
     * Post an API call update to a specific call group
     *
     * @param groupId ID for the Call Group
     */
    fun postCallGroupUpdate(groupId: Long) {
        if (!requestedCallGroups.any { it.groupId == groupId }) return

        val indexOfSpecifiedCallGroup = completedCallGroups.indexOfFirst { it.groupId == groupId }

        when {
            indexOfSpecifiedCallGroup > -1 -> {
                val specifiedCallGroup = completedCallGroups[indexOfSpecifiedCallGroup]
                val callsMade = specifiedCallGroup.calls + 1

                specifiedCallGroup.calls = callsMade

                completedCallGroups.removeAt(indexOfSpecifiedCallGroup)
                completedCallGroups += specifiedCallGroup
            }
            else -> {
                completedCallGroups += CallGroup(groupId, 1)
            }
        }

        checkForApiCallsCompletion()
    }

    /**
     * Post an API call update to a specific call group and also, in case of need,
     * increase the number of calls to expect from that group (for example, when a request fails
     * and we need to perform an extra request which wasn't accounted at the creation of the CallGroup)
     *
     * @param groupId    ID for the specific call group
     * @param callsToAdd No of calls to add to that group
     */
    fun postCallGroupUpdate(groupId: Long, callsToAdd: Int) {
        val indexOfSpecifiedCallGroup = requestedCallGroups.indexOfFirst { it.groupId == groupId }

        if (indexOfSpecifiedCallGroup > -1) {
            val specifiedCallGroup = requestedCallGroups[indexOfSpecifiedCallGroup]
            val callsToMake = specifiedCallGroup.calls + callsToAdd

            specifiedCallGroup.calls = callsToMake

            requestedCallGroups.removeAt(indexOfSpecifiedCallGroup)
            requestedCallGroups += specifiedCallGroup
        }

        postCallGroupUpdate(groupId)
    }

    /* ********************************************
     *               Private methods              *
     ******************************************** */

    /**
     * Check whether all specified API calls are completed
     */
    private fun checkForApiCallsCompletion() {
        var allCallsComplete = true

        requestedCallGroups.forEach { callGroup ->
            if (callGroup !in completedCallGroups) {
                allCallsComplete = false
                return@forEach
            } else {
                val indexOfSelectedCallGroup = completedCallGroups.indexOf(callGroup)
                val selectedGroup = completedCallGroups[indexOfSelectedCallGroup]

                if (selectedGroup.calls < callGroup.calls) {
                    allCallsComplete = false
                    return@forEach
                }
            }
        }

        //If all calls are made then fire a callback to the listener
        if (allCallsComplete) {
            onCallsCompleteListener.onCallsCompleted()
        }
    }
}