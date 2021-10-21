/*
 * Copyright (c) 2020 Huawei Device Co., Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ycbjie.slide;

import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

public class LoggerUtils {
    private static final String TAG_LOG = "SlideLayout";

    private static final int DOMAIN_ID = 0xD000F00;

    private static final HiLogLabel LABEL_LOG = new HiLogLabel(3, DOMAIN_ID, LoggerUtils.TAG_LOG);

    private static final String LOG_FORMAT = "%{public}s: %{public}s";

    private static boolean isLog = false;

    public static void setIsLog(boolean isLog) {
        LoggerUtils.isLog = isLog;
    }

    private LoggerUtils() {
        /* Do nothing */
    }

    /**
     * Print debug log
     *
     * @param tag log tag
     * @param msg log message
     */
    public static void d(String tag, String msg) {
        if (isLog) {
            HiLog.debug(LABEL_LOG, LOG_FORMAT, tag, msg);
        }
    }

    /**
     * Print info log
     *
     * @param tag log tag
     * @param msg log message
     */
    public static void i(String tag, String msg) {
        if (isLog) {
            HiLog.info(LABEL_LOG, LOG_FORMAT, tag, msg);
        }
    }

}

