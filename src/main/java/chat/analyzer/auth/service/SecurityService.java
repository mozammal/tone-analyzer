package chat.analyzer.auth.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Created by mozammal on 4/18/17. */
public interface SecurityService {

  void autoLogin(
      String username, String password, HttpServletRequest request, HttpServletResponse response);
}
