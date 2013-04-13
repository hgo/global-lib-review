package services;


import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class GuiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(AdminService.class).to(AdminServiceImpl.class).in(Singleton.class);
        bind(GithubService.class).to(GithubServiceImpl.class).in(Singleton.class);
    }
}