/*
 * Copyright 2022 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.keepshare.libcurlnetworkdemo.transform;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;


import com.keepshare.libcurlnetworkdemo.curl.CurlNetEngine;

import java.util.concurrent.Executors;

abstract class RequestResponseConverterBasedBuilder<
    ConvertBuilder extends RequestResponseConverterBasedBuilder<?, ? extends ObjectBuilt>,
    ObjectBuilt> {
  private static final int DEFAULT_THREAD_POOL_SIZE = 4;

  private final CurlNetEngine netEngine;
  private int uploadDataProviderExecutorSize = DEFAULT_THREAD_POOL_SIZE;

  private final ConvertBuilder castedThis;

  RequestResponseConverterBasedBuilder(CurlNetEngine netEngine, Class<ConvertBuilder> clazz) {
    this.netEngine = checkNotNull(netEngine);
    checkArgument(this.getClass().equals(clazz));
    castedThis = (ConvertBuilder) this;
  }


  public final ConvertBuilder setUploadDataProviderExecutorSize(int size) {
    checkArgument(size > 0, "The number of threads must be positive!");
    uploadDataProviderExecutorSize = size;
    return castedThis;
  }


  abstract ObjectBuilt build(RequestResponseConverter converter);

  public final ObjectBuilt build() {

    RequestResponseConverter converter =
        new RequestResponseConverter(
            netEngine,
            new ResponseConverterImpl(),
            RequestBodyConverterImpl.create(Executors.newCachedThreadPool()));

    return build(converter);
  }
}
