/*
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
 */
package app.jpetstore.account;

import app.jpetstore.account.domain.Account;
import app.jpetstore.account.service.AccountService;
import app.jpetstore.catalog.domain.Product;
import app.jpetstore.catalog.service.CatalogService;
import app.jpetstore.common.validation.BeanValidator;
import app.jpetstore.user.UserSession;
import app.jpetstore.user.UserSessionManager;
import com.aspectran.core.activity.Translet;
import com.aspectran.core.component.bean.annotation.Autowired;
import com.aspectran.core.component.bean.annotation.Component;
import com.aspectran.core.component.bean.annotation.Dispatch;
import com.aspectran.core.component.bean.annotation.Redirect;
import com.aspectran.core.component.bean.annotation.Request;
import com.aspectran.core.component.bean.annotation.RequestToPost;
import com.aspectran.utils.StringUtils;
import com.aspectran.utils.annotation.jsr305.NonNull;
import com.aspectran.web.support.http.HttpStatusSetter;

import java.util.List;
import java.util.Map;

/**
 * The Class AccountActivity.
 *
 * @author Juho Jeong
 */
@Component
public class AccountActivity {

    public AccountService accountService;

    public CatalogService catalogService;

    public UserSessionManager sessionManager;

    @Autowired
    public AccountActivity(AccountService accountService,
                           CatalogService catalogService,
                           UserSessionManager sessionManager) {
        this.accountService = accountService;
        this.catalogService = catalogService;
        this.sessionManager = sessionManager;
    }

    @Request("/account/newAccountForm")
    @Dispatch("account/NewAccountForm")
    public void newAccountForm() {
    }

    /**
     * New account.
     */
    @RequestToPost("/account/newAccount")
    @Redirect("/account/signonForm?created=true")
    public void newAccount(Translet translet,
                           Account account,
                           @NonNull BeanValidator beanValidator) {
        beanValidator.validate(account, Account.Create.class);
        if (beanValidator.hasErrors()) {
            HttpStatusSetter.badRequest(translet);
            translet.setAttribute("account", account);
            translet.setAttribute("errors", beanValidator.getErrors());
            translet.forward("/account/newAccountForm");
            return;
        }

        try {
            translet.setAttribute("account", account);
            translet.setAttribute("errors", Map.of(
                    "usernameDuplicated", translet.getMessage("common.validation.usernameDuplicated.message")));
            accountService.insertAccount(account);
        } catch (Exception e) {
            translet.forward("/account/newAccountForm");
        }
    }

    /**
     * Edits the account form.
     */
    @Request("/account/editAccountForm")
    @Dispatch("account/EditAccountForm")
    public void editAccountForm(@NonNull Translet translet) {
        if (translet.getAttribute("account") == null) {
            translet.setAttribute("account", sessionManager.get().getAccount());
        } else {
            Account account = translet.getAttribute("account");
            account.setUsername(sessionManager.get().getAccount().getUsername());
        }
    }

    /**
     * Edits the account.
     */
    @RequestToPost("/account/editAccount")
    @Redirect("/account/editAccountForm?updated=true")
    public void editAccount(@NonNull Translet translet,
                            Account account,
                            @NonNull BeanValidator beanValidator) {
        beanValidator.validate(account);
        if (beanValidator.hasErrors()) {
            HttpStatusSetter.badRequest(translet);
            translet.setAttribute("account", account);
            translet.setAttribute("errors", beanValidator.getErrors());
            translet.forward("/account/editAccountForm");
            return;
        }

        String username = sessionManager.get().getAccount().getUsername();
        account.setUsername(username);
        accountService.updateAccount(account);

        Account account2 = accountService.getAccount(account.getUsername());
        List<Product> products = catalogService.getProductListByCategory(account2.getFavouriteCategoryId());
        UserSession userSession = sessionManager.get();
        userSession.setAccount(account2);
        userSession.setProducts(products);
        sessionManager.save(userSession);
    }

    /**
     * Signon form.
     */
    @Request("/account/signonForm")
    @Dispatch("account/SignonForm")
    public void signonForm() {
    }

    /**
     * Signon.
     */
    @RequestToPost("/account/signon")
    @Redirect("/")
    public void signon(Translet translet,
                       String username,
                       String password,
                       String referer) {
        Account account = accountService.getAccount(username, password);
        if (account == null) {
            translet.redirect("/account/signonForm?retry=true");
        } else {
            account.setPassword(null);
            List<Product> products = catalogService.getProductListByCategory(account.getFavouriteCategoryId());
            UserSession userSession = sessionManager.get();
            userSession.setAccount(account);
            userSession.setProducts(products);
            userSession.setAuthenticated(true);
            sessionManager.save(userSession);
            if (StringUtils.hasLength(referer)) {
                translet.redirect(referer);
            }
        }
    }

    /**
     * Signoff.
     */
    @Request("/account/signoff")
    @Redirect("/")
    public void signoff() {
        sessionManager.expire();
    }

}
