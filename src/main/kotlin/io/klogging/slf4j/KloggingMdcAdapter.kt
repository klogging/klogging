/*

   Copyright 2021 Michael Strasser.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

*/

package io.klogging.slf4j

import org.slf4j.spi.MDCAdapter

class KloggingMdcAdapter : MDCAdapter {
    override fun put(key: String?, value: String?) {
        TODO("Not yet implemented")
    }

    override fun get(key: String?): String {
        TODO("Not yet implemented")
    }

    override fun remove(key: String?) {
        TODO("Not yet implemented")
    }

    override fun clear() {
        TODO("Not yet implemented")
    }

    override fun getCopyOfContextMap(): MutableMap<String, String> {
        TODO("Not yet implemented")
    }

    override fun setContextMap(contextMap: MutableMap<String, String>?) {
        TODO("Not yet implemented")
    }
}
