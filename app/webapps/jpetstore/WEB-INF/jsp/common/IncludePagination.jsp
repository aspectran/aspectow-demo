<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%--
 * Copyright (c) 2018-present The Aspectran Project
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
--%>
<%--
  Common Pagination Include
  Uses URLSearchParams to retain all existing query parameters automatically.
--%>
<c:if test="${pageInfo != null and pageInfo.totalPages > 1}">
    <nav class="pagination-container" aria-label="Page navigation">
        <ul class="pagination justify-content-center mt-3 mb-0">
            <!-- First -->
            <li class="page-item ${pageInfo.number <= 1 ? 'disabled' : ''}">
                <c:if test="${pageInfo.number > 1}">
                    <a class="page-link" href="javascript:void(0)" onclick="gotoPage(1)" title="First" aria-label="First page">First</a>
                </c:if>
                <c:if test="${pageInfo.number <= 1}">
                    <span class="page-link" title="First" aria-label="First page">First</span>
                </c:if>
            </li>

            <!-- Previous (5 Pages Block) -->
            <li class="page-item ${pageInfo.number <= 5 ? 'disabled' : ''}">
                <c:if test="${pageInfo.number > 5 and pageInfo.number % 5 == 1}">
                    <a class="page-link" href="javascript:void(0)" onclick="gotoPage(${pageInfo.number - 1})" title="Previous" aria-label="Previous page">&laquo;</a>
                </c:if>
                <c:if test="${pageInfo.number > 5 and pageInfo.number % 5 != 1}">
                    <a class="page-link" href="javascript:void(0)" onclick="gotoPage(${pageInfo.number - 5})" title="Previous" aria-label="Previous page">&laquo;</a>
                </c:if>
                <c:if test="${pageInfo.number <= 5}">
                    <span class="page-link" title="Previous" aria-label="Previous page">&laquo;</span>
                </c:if>
            </li>

            <!-- Page Number Sequence (5 per block) -->
            <c:forEach var="i" begin="${pageInfo.number - (pageInfo.number - 1) % 5}" end="${pageInfo.number - (pageInfo.number - 1) % 5 + 4}">
                <c:if test="${i <= pageInfo.totalPages}">
                    <li class="page-item ${i == pageInfo.number ? 'active' : ''}">
                        <c:if test="${i != pageInfo.number}">
                            <a class="page-link" href="javascript:void(0)" onclick="gotoPage(${i})" aria-label="Page ${i}">${i}</a>
                        </c:if>
                        <c:if test="${i == pageInfo.number}">
                            <span class="page-link" aria-label="Page ${i}">${i}</span>
                        </c:if>
                    </li>
                </c:if>
            </c:forEach>

            <!-- Next (5 Pages Block) -->
            <li class="page-item ${pageInfo.number > pageInfo.totalPages - 5 and pageInfo.number >= pageInfo.totalPages - (pageInfo.totalPages - 1) % 5 ? 'disabled' : ''}">
                <c:if test="${pageInfo.number < pageInfo.totalPages - 5 and pageInfo.number % 5 == 0}">
                    <a class="page-link" href="javascript:void(0)" onclick="gotoPage(${pageInfo.number + 1})" title="Next" aria-label="Next page">&raquo;</a>
                </c:if>
                <c:if test="${pageInfo.number < pageInfo.totalPages - 5 and pageInfo.number % 5 != 0}">
                    <a class="page-link" href="javascript:void(0)" onclick="gotoPage(${pageInfo.number + 5})" title="Next" aria-label="Next page">&raquo;</a>
                </c:if>
                <c:if test="${pageInfo.number >= pageInfo.totalPages - 5 and pageInfo.number < pageInfo.totalPages - (pageInfo.totalPages - 1) % 5}">
                    <a class="page-link" href="javascript:void(0)" onclick="gotoPage(${pageInfo.totalPages})" title="Next" aria-label="Next page">&raquo;</a>
                </c:if>
                <c:if test="${pageInfo.number >= pageInfo.totalPages - (pageInfo.totalPages - 1) % 5}">
                    <span class="page-link" title="Next" aria-label="Next page">&raquo;</span>
                </c:if>
            </li>

            <!-- Last -->
            <li class="page-item ${pageInfo.number >= pageInfo.totalPages ? 'disabled' : ''}">
                <c:if test="${pageInfo.number < pageInfo.totalPages}">
                    <a class="page-link" href="javascript:void(0)" onclick="gotoPage(${pageInfo.totalPages})" title="Last" aria-label="Last page">Last</a>
                </c:if>
                <c:if test="${pageInfo.number >= pageInfo.totalPages}">
                    <span class="page-link" title="Last" aria-label="Last page">Last</span>
                </c:if>
            </li>
        </ul>
        <script>
            window.gotoPage = window.gotoPage || function(page) {
                const urlParams = new URLSearchParams(window.location.search);
                urlParams.set('page', page);
                window.location.search = urlParams.toString();
            };
        </script>
    </nav>
</c:if>
