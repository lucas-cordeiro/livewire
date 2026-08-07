package com.livewire.plugin.logs

internal expect fun currentTimeMillis(): Long

internal expect fun formatTimestamp(epochMillis: Long): String
