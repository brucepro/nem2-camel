/*
 * Copyright 2018 NEM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.nem.camel.models

import com.fasterxml.jackson.annotation.JsonProperty

class Payload {
  @JsonProperty("payload") var payload: Object = _
  @JsonProperty("hash") var hash: String = _
  @JsonProperty("address") var address: String = _
}

class CosignaturePayload {
  @JsonProperty("parentHash") var parentHash: String = _
  @JsonProperty("signature") var signature: Object = _
  @JsonProperty("signer") var signer: Object = _
  @JsonProperty("address") var address: String = _
}
