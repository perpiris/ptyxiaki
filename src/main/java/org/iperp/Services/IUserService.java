package org.iperp.Services;

import java.util.Map;

public interface IUserService {

    Map<Long, Integer> getUserSkills(String username);
}
