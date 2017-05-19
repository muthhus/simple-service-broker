/**
 Copyright (C) 2016-Present Pivotal Software, Inc. All rights reserved.

 This program and the accompanying materials are made available under
 the terms of the under the Apache License, Version 2.0 (the "License”);
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package io.pivotal.ecosystem.servicebroker;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.springframework.web.bind.annotation.RequestBody;

interface HelloBrokerRepository {
    @Headers("Content-Type: application/json")
    @RequestLine("POST /users")
    User provisionUser(@RequestBody User user);

    @Headers("Content-Type: application/json")
    @RequestLine("DELETE /users/{username}")
    User deprovisionUser(@Param(value = "username") String username);

    @Headers("Content-Type: application/json")
    @RequestLine("PUT /users")
    User updateUser(@RequestBody User user);
}