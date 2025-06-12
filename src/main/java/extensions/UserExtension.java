package extensions;

import models.CreateUserRequestModel;
import org.junit.jupiter.api.extension.*;
import requests.steps.AdminSteps;
import requests.steps.UserSteps;

public class UserExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

    private static final String USER_REQUEST_KEY = "userRequest";
    private static final String USER_ID_KEY = "userId";

    private ExtensionContext.Store getStore(ExtensionContext context) {
        return context.getStore(ExtensionContext.Namespace.create(getClass(), context.getRequiredTestMethod()));
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        CreateUserRequestModel userRequest = AdminSteps.createUser();
        Long userId = AdminSteps.getCreatedUserId();

        getStore(context).put(USER_REQUEST_KEY, userRequest);
        getStore(context).put(USER_ID_KEY, userId);
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
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