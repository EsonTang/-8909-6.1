#
# Copyright (C) 2013 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

LOCAL_PATH := $(call my-dir)

#
# Build app code.
#
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_STATIC_JAVA_LIBRARIES := android-support-v4 \
                               android-support-v13 \
                               android-support-v7-recyclerview \
                               android-support-v7-recyclerview-res \
                               guava
LOCAL_JAVA_LIBRARIES := telephony-common

LOCAL_SRC_FILES := $(call all-java-files-under, src) \
    $(call all-java-files-under, WallpaperPicker/src) \
    $(call all-renderscript-files-under, src) \
    $(call all-proto-files-under, protos)
LOCAL_RESOURCE_DIR := $(LOCAL_PATH)/WallpaperPicker/res $(LOCAL_PATH)/res \
                      $(TOP)/frameworks/support/v7/recyclerview/res

LOCAL_AAPT_FLAGS := --auto-add-overlay

LOCAL_PROTOC_OPTIMIZE_TYPE := nano
LOCAL_PROTOC_FLAGS := --proto_path=$(LOCAL_PATH)/protos/

# LOCAL_SDK_VERSION := 21

LOCAL_PACKAGE_NAME := Trebuchet
LOCAL_PRIVILEGED_MODULE := true
LOCAL_CERTIFICATE := shared

LOCAL_PROGUARD_FLAG_FILES := proguard.flags
LOCAL_PROGUARD_ENABLED := disabled

LOCAL_OVERRIDES_PACKAGES += Home Launcher2 Launcher3  BasicDreams  QualcommSettings QuickSearchBox  WfdClient CNEService FidoCryptoService QtiTetherService SoundRecorder NativeAudioLatency WfdService

include $(BUILD_PACKAGE)

include $(call all-makefiles-under,$(LOCAL_PATH))
