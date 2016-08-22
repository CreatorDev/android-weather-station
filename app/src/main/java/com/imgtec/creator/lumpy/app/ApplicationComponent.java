/*
 * <b>Copyright 2016 by Imagination Technologies Limited
 * and/or its affiliated group companies.</b>
 *
 * All rights reserved.  No part of this software, either
 * material or conceptual may be copied or distributed,
 * transmitted, transcribed, stored in a retrieval system
 * or translated into any human or computer language in any
 * form by any means, electronic, mechanical, manual or
 * other-wise, or disclosed to the third parties without the
 * express written permission of Imagination Technologies
 * Limited, Home Park Estate, Kings Langley, Hertfordshire,
 * WD4 8LZ, U.K.
 */

package com.imgtec.creator.lumpy.app;

import com.imgtec.creator.lumpy.LoginActivity;
import com.imgtec.creator.lumpy.MainActivity;
import com.imgtec.creator.lumpy.data.api.ApiModule;
import com.imgtec.creator.lumpy.db.DBManager;
import com.imgtec.creator.lumpy.db.DBModule;
import com.imgtec.creator.lumpy.fragments.DashboardFragment;
import com.imgtec.creator.lumpy.fragments.LoginFragment;
import com.imgtec.di.PerApp;

import dagger.Component;

@PerApp
@Component(
    modules = {
        ApplicationModule.class,
        ApiModule.class,
        DBModule.class
    }
)
public interface ApplicationComponent {

  final class Initializer {

    private Initializer() {}

    static ApplicationComponent init(App application) {
      return DaggerApplicationComponent
          .builder()

          .applicationModule(new ApplicationModule(application))
          .apiModule(new ApiModule())
          .build();
    }
  }

  App inject(App app);
  void inject(LoginActivity loginActivity);
  void inject(MainActivity mainActivity);
  void inject(DashboardFragment dashboardFragment);
  void inject(LoginFragment loginFragment);


}

