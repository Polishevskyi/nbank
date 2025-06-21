package common.extensions;

import api.models.CreateUserRequestModel;
import api.requests.steps.AdminSteps;
import api.requests.steps.UserSteps;
import common.annotations.UserSession;
import common.storage.SessionStorage;
import org.junit.jupiter.api.extension.*;
import ui.pages.BasePage;
import java.util.ArrayList;
import java.util.List;

public class UserSessionExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {
    private static final String USER_REQUEST_KEY = "userRequest";
    private static final String USER_ID_KEY = "userId";

    private ExtensionContext.Store getStore(ExtensionContext context) {
        return context.getStore(ExtensionContext.Namespace.create(getClass(), context.getRequiredTestMethod()));
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        UserSession annotation = context.getRequiredTestMethod().getAnnotation(UserSession.class);
        if (annotation == null) {
            return;
        }
        SessionStorage.clear();
        CreateUserRequestModel userRequest = AdminSteps.createUser();
        Long userId = AdminSteps.getCreatedUserId();
        List<CreateUserRequestModel> users = new ArrayList<>();
        users.add(userRequest);
        SessionStorage.addUsers(users);
        BasePage.authAsUser(userRequest);
        getStore(context).put(USER_REQUEST_KEY, userRequest);
        getStore(context).put(USER_ID_KEY, userId);
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        UserSession annotation = context.getRequiredTestMethod().getAnnotation(UserSession.class);
        if (annotation == null) {
            return;
        }
        Long userId = getStore(context).get(USER_ID_KEY, Long.class);
        if (userId != null) {
            UserSteps.deleteUser(userId);
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        Class<?> parameterType = parameterContext.getParameter().getType();
        return parameterType == CreateUserRequestModel.class || parameterType == Long.class;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        Class<?> parameterType = parameterContext.getParameter().getType();
        if (parameterType == CreateUserRequestModel.class) {
            return getStore(extensionContext).get(USER_REQUEST_KEY, CreateUserRequestModel.class);
        }
        if (parameterType == Long.class) {
            return getStore(extensionContext).get(USER_ID_KEY, Long.class);
        }
        throw new IllegalArgumentException("Unsupported parameter type: " + parameterType);
    }
}
