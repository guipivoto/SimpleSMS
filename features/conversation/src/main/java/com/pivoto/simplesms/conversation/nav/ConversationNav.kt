package com.pivoto.simplesms.conversation.nav

import com.pivoto.simplesms.contract.Feature
import com.pivoto.simplesms.contract.FeatureEvents

sealed interface ConversationNav : Feature<ConversationScreenEvents>

interface ConversationScreenEvents : FeatureEvents