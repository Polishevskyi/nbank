package common.storage;

import api.models.CreateUserRequestModel;
import api.requests.steps.UserSteps;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class SessionStorage {
    private static final SessionStorage INSTANCE = new SessionStorage();

    private final LinkedHashMap<CreateUserRequestModel, UserSteps> userStepsMap = new LinkedHashMap<>();

    private SessionStorage() {
    }

    public static void addUsers(List<CreateUserRequestModel> users) {
        for (CreateUserRequestModel user : users) {
            INSTANCE.userStepsMap.put(user, new UserSteps(user.getUsername(), user.getPassword()));
        }
    }

    /**
     * Returns the CreateUserRequest object by its ordinal number in the list of created users.
     *
     * @param number Ordinal number, starting from 1 (not 0).
     * @return CreateUserRequest object corresponding to the specified ordinal number.
     */
    public static CreateUserRequestModel getUser(int number) {
        return new ArrayList<>(INSTANCE.userStepsMap.keySet()).get(number - 1);
    }

    public static CreateUserRequestModel getUser() {
        return getUser(1);
    }

    public static UserSteps getSteps(int number) {
        return new ArrayList<>(INSTANCE.userStepsMap.values()).get(number - 1);
    }

    public static UserSteps getSteps() {
        return getSteps(1);
    }

    public static void clear() {
        INSTANCE.userStepsMap.clear();
    }
}
