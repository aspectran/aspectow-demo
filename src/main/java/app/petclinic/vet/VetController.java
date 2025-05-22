/*
 * Copyright (c) 2012-present-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package app.petclinic.vet;

import app.petclinic.common.pagination.PageInfo;
import com.aspectran.core.activity.Translet;
import com.aspectran.core.component.bean.annotation.Autowired;
import com.aspectran.core.component.bean.annotation.Component;
import com.aspectran.core.component.bean.annotation.Dispatch;
import com.aspectran.core.component.bean.annotation.Request;
import com.aspectran.utils.annotation.jsr305.NonNull;

import java.util.List;

/**
 * @author Juergen Hoeller
 * @author Mark Fisher
 * @author Ken Krebs
 * @author Arjen Poutsma
 */
@Component
public class VetController {

	private final VetDao vetDao;

	@Autowired
    public VetController(VetDao vetDao) {
		this.vetDao = vetDao;
	}

    @Request("/vets.html")
    @Dispatch("vets/vetList")
	public void vetList(@NonNull Translet translet) {
        PageInfo pageInfo = PageInfo.of(translet);
        List<Vet> listVets = vetDao.findAll(pageInfo);

        translet.setAttribute("listVets", listVets);
        translet.setAttribute("pageInfo", pageInfo);
	}

}
