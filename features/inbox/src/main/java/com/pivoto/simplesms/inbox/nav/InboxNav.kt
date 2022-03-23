package com.pivoto.simplesms.inbox.nav

import com.pivoto.simplesms.contract.Feature
import com.pivoto.simplesms.contract.FeatureEvents

sealed interface InboxNav : Feature<InboxScreenEvents>

interface InboxScreenEvents : FeatureEvents {

    fun onConversationOpened(messageAddress: String)
}