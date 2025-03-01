package ca.uhn.fhir.jpa.sched;

/*-
 * #%L
 * hapi-fhir-jpa
 * %%
 * Copyright (C) 2014 - 2023 Smile CDR, Inc.
 * %%
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
 * #L%
 */

import ca.uhn.fhir.jpa.model.sched.IHapiScheduler;

public class HapiSchedulerServiceImpl extends BaseSchedulerServiceImpl {
	public static final String THREAD_NAME_PREFIX = "hapi-fhir-jpa-scheduler";

	@Override
	protected IHapiScheduler getLocalHapiScheduler() {
		return new LocalHapiScheduler(THREAD_NAME_PREFIX, mySchedulerJobFactory);
	}

	@Override
	protected IHapiScheduler getClusteredScheduler() {
		return new ClusteredHapiScheduler(THREAD_NAME_PREFIX, mySchedulerJobFactory);
	}
}
