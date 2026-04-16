package com.example.companent;

import com.example.controller.AccountSettingsView;
import com.example.controller.ContactUsView;
import com.example.controller.ExpensesView;
import com.example.controller.HomeView;
import com.example.controller.LoginView;
import com.example.controller.ProfileView;
import com.example.controller.RegistrationView;
import com.example.controller.StatisticsView;
import com.example.entity.ProfileEntity;
import com.example.repository.ProfileRepository;
import com.example.service.ProfileSettingService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.RouterLink;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class MainLayout extends AppLayout implements AfterNavigationObserver {

    private final Map<Class<? extends Component>, RouterLink> linkMap = new HashMap<>();
    private final ProfileRepository profileRepository;
    private final ProfileSettingService profileSettingService;

    private Span userNameLabel;
    private Image avatar;

    public MainLayout(ProfileRepository profileRepository, ProfileSettingService profileSettingService) {
        this.profileRepository = profileRepository;
        this.profileSettingService = profileSettingService;

        boolean isAuthenticated = isUserAuthenticated();
        String username = getCurrentUsername();

        H3 logoText = new H3();
        logoText.getElement().setProperty("innerHTML",
                "<span style='background:linear-gradient(135deg,#60a5fa,#a78bfa,#22d3ee);" +
                        "-webkit-background-clip:text;" +
                        "-webkit-text-fill-color:transparent;" +
                        "background-clip:text;'>" +
                        "Sarhisob</span>"
        );

        logoText.getStyle()
                .set("margin", "0")
                .set("font-size", "22px")
                .set("font-weight", "900")
                .set("letter-spacing", "0.6px")
                .set("font-family", "'Inter','Segoe UI',sans-serif");

        HorizontalLayout logoSection = new HorizontalLayout(logoText);
        logoSection.setAlignItems(FlexComponent.Alignment.CENTER);
        logoSection.setSpacing(true);
        logoSection.getStyle()
                .set("gap", "10px")
                .set("margin-left", "80px");

        HorizontalLayout centerLinks = new HorizontalLayout();
        centerLinks.setAlignItems(FlexComponent.Alignment.CENTER);
        centerLinks.setSpacing(true);
        centerLinks.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        centerLinks.setWidthFull();
        centerLinks.getStyle().set("gap", "4px");

        RouterLink home = createNavLink("Home", HomeView.class);
        RouterLink contactUs = createNavLink("Contact Us", ContactUsView.class);

        centerLinks.add(home);

        if (isAuthenticated) {
            RouterLink expenses = createNavLink("Expenses", ExpensesView.class);
            RouterLink statistics = createNavLink("Statistics", StatisticsView.class);
            centerLinks.add(expenses, statistics);
        }

        centerLinks.add(contactUs);

        HorizontalLayout rightActions = new HorizontalLayout();
        rightActions.setAlignItems(FlexComponent.Alignment.CENTER);
        rightActions.setSpacing(true);
        rightActions.getStyle().set("gap", "8px");

        if (isAuthenticated) {
            Optional<ProfileEntity> optionalProfile =
                    profileRepository.findByEmailAndVisibleIsTrue(username);

            String fullName = username;
            String avatarSrc = "/images/avatar.png";
            boolean hasCustomPhoto = false;

            if (optionalProfile.isPresent()) {
                ProfileEntity profile = optionalProfile.get();

                String name = profile.getName() != null ? profile.getName() : "";
                String surname = profile.getSurname() != null ? profile.getSurname() : "";
                String combined = (surname + " " + name).trim();

                if (!combined.isBlank()) {
                    fullName = combined;
                }

                if (profile.getPhoto() != null && profile.getPhoto().getId() != null) {
                    avatarSrc = "/profiles/" + profile.getPhoto().getId();
                    hasCustomPhoto = true;
                }
            }

            MenuBar userMenu = new MenuBar();
            userMenu.addThemeVariants(MenuBarVariant.LUMO_TERTIARY);
            userMenu.getStyle()
                    .set("padding", "0")
                    .set("background", "transparent")
                    .set("border", "none")
                    .set("box-shadow", "none")
                    .set("min-height", "auto")
                    .set("margin-right","90px");

            MenuItem userRoot = userMenu.addItem(createUserMenuContent(avatarSrc, fullName, hasCustomPhoto));
            userRoot.getElement().getStyle()
                    .set("padding", "0")
                    .set("margin", "0")
                    .set("background", "transparent")
                    .set("border-radius", "18px");

            userRoot.getSubMenu().addItem("Profile",
                    e -> UI.getCurrent().navigate(ProfileView.class));

            userRoot.getSubMenu().addItem("Profile Settings",
                    e -> UI.getCurrent().navigate(AccountSettingsView.class));

            Span logoutText = new Span("Logout");
            logoutText.getStyle().set("color", "var(--lumo-error-text-color)");

            userRoot.getSubMenu().addItem(logoutText,
                    e -> UI.getCurrent().getPage().setLocation("/logout"));

            rightActions.add(userMenu);

        } else {
            RouterLink login = new RouterLink("Login", LoginView.class);
            login.getStyle()
                    .set("text-decoration", "none")
                    .set("font-weight", "600")
                    .set("font-size", "14px")
                    .set("color", "#94a3b8")
                    .set("padding", "8px 16px")
                    .set("border-radius", "10px")
                    .set("transition", "all 0.3s ease");

            Button signUp = new Button("Sign Up");
            signUp.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            signUp.getStyle()
                    .set("background", "#2563eb")
                    .set("color", "white")
                    .set("font-weight", "700")
                    .set("font-size", "14px")
                    .set("border-radius", "10px")
                    .set("padding", "8px 20px")
                    .set("border", "none")
                    .set("box-shadow", "0 4px 16px rgba(37,99,235,0.4)")
                    .set("transition", "all 0.3s ease")
                    .set("cursor", "pointer");

            signUp.addClickListener(e ->
                    signUp.getUI().ifPresent(ui -> ui.navigate(RegistrationView.class)));

            rightActions.getStyle().set("margin-right", "100px");
            rightActions.add(login, signUp);
        }

        HorizontalLayout navbar = new HorizontalLayout(logoSection, centerLinks, rightActions);
        navbar.setWidthFull();
        navbar.setAlignItems(FlexComponent.Alignment.CENTER);
        navbar.setFlexGrow(1, centerLinks);

        navbar.getStyle()
                .set("padding", "12px 40px")
                .set("background", "#0b1120")
                .set("border-bottom", "1px solid rgba(255,255,255,0.07)")
                .set("box-shadow", "0 2px 24px rgba(0,0,0,0.4), 0 1px 0 rgba(59,130,246,0.07)");

        addToNavbar(navbar);
    }

    private boolean isUserAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)
                && !"anonymousUser".equals(authentication.getName());
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : "anonymousUser";
    }

    private RouterLink createNavLink(String text, Class<? extends Component> target) {
        RouterLink link = new RouterLink(text, target);

        link.getStyle()
                .set("text-decoration", "none")
                .set("font-weight", "600")
                .set("font-size", "14px")
                .set("color", "#94a3b8")
                .set("padding", "9px 18px")
                .set("border-radius", "10px")
                .set("letter-spacing", "0.01em")
                .set("transition", "all 0.3s ease")
                .set("font-family", "'Inter', 'Segoe UI', sans-serif");

        link.getElement().addEventListener("mouseenter", e -> {
            if (!link.getElement().hasAttribute("highlight")) {
                link.getStyle()
                        .set("background", "rgba(255,255,255,0.07)")
                        .set("color", "#e2e8f0");
            }
        });

        link.getElement().addEventListener("mouseleave", e -> {
            if (!link.getElement().hasAttribute("highlight")) {
                link.getStyle()
                        .set("background", "transparent")
                        .set("color", "#94a3b8");
            }
        });

        linkMap.put(target, link);
        return link;
    }

    private Component createUserMenuContent(String avatarSrc, String fullName, boolean hasCustomPhoto) {
        HorizontalLayout root = new HorizontalLayout();
        root.setPadding(false);
        root.setSpacing(false);
        root.setAlignItems(FlexComponent.Alignment.CENTER);

        root.getStyle()
                .set("gap", "10px")
                .set("padding", "4px 10px 4px 6px")
                .set("border-radius", "14px")
                .set("background", "rgba(255,255,255,0.03)")
                .set("border", "1px solid rgba(255,255,255,0.05)")
                .set("box-shadow", "inset 0 1px 0 rgba(255,255,255,0.02)")
                .set("transition", "all 0.2s ease")
                .set("cursor", "pointer");

        Div avatarWrap = new Div();
        avatarWrap.getStyle()
                .set("width", "38px")
                .set("height", "38px")
                .set("min-width", "38px")
                .set("border-radius", "50%")
                .set("overflow", "hidden")
                .set("position", "relative")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("background", "rgba(255,255,255,0.04)")
                .set("border", "1px solid rgba(255,255,255,0.06)");

        String initials = getInitialsFromName(fullName);

        Span fallback = new Span(initials);
        fallback.getStyle()
                .set("position", "absolute")
                .set("inset", "0")
                .set("display", hasCustomPhoto ? "none" : "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("color", "#e2e8f0")
                .set("font-size", "12px")
                .set("font-weight", "800")
                .set("z-index", "1");

        avatarWrap.add(fallback);

        if (avatarSrc != null && !avatarSrc.isBlank()) {
            Image avatarImage = new Image(avatarSrc, fullName);
            avatarImage.getStyle()
                    .set("position", "absolute")
                    .set("inset", "0")
                    .set("width", "100%")
                    .set("height", "100%")
                    .set("object-fit", "cover")
                    .set("object-position", "center")
                    .set("display", "block")
                    .set("z-index", "2");

            avatarImage.getElement().addEventListener("error", e -> {
                avatarImage.setVisible(false);
                fallback.getStyle().set("display", "flex");
            });

            avatarWrap.add(avatarImage);
        }

        Span name = new Span(fullName);
        name.getStyle()
                .set("color", "#e5edf9")
                .set("font-size", "14px")
                .set("font-weight", "700")
                .set("white-space", "nowrap")
                .set("overflow", "hidden")
                .set("text-overflow", "ellipsis")
                .set("max-width", "170px");

        Icon chevron = VaadinIcon.CHEVRON_DOWN.create();
        chevron.setSize("15px");
        chevron.getStyle()
                .set("color", "#60a5fa")
                .set("flex-shrink", "0")
                .set("transition", "transform 0.2s ease");

        root.add(avatarWrap, name, chevron);

        root.getElement().addEventListener("mouseenter", e -> {
            root.getStyle()
                    .set("background", "rgba(255,255,255,0.05)")
                    .set("border-color", "rgba(96,165,250,0.10)");
            chevron.getStyle().set("transform", "translateY(1px)");
        });

        root.getElement().addEventListener("mouseleave", e -> {
            root.getStyle()
                    .set("background", "rgba(255,255,255,0.03)")
                    .set("border-color", "rgba(255,255,255,0.05)");
            chevron.getStyle().set("transform", "translateY(0)");
        });

        return root;
    }

    private String getInitialsFromName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            return "?";
        }

        String[] parts = fullName.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, 1).toUpperCase();
        }

        String first = parts[0].substring(0, 1).toUpperCase();
        String last = parts[parts.length - 1].substring(0, 1).toUpperCase();
        return first + last;
    }

    public void refreshUserInfo() {
        var dto = profileSettingService.getCurrentPersonalInfo();

        String fullName = ((dto.getSurname() == null ? "" : dto.getSurname()) + " "
                + (dto.getName() == null ? "" : dto.getName())).trim();

        if (fullName.isBlank()) {
            fullName = dto.getUsername() != null ? dto.getUsername() : "User";
        }

        if (userNameLabel != null) {
            userNameLabel.setText(fullName);
        }

        String photoId = dto.getPhotoId();
        if (avatar != null) {
            if (photoId != null && !photoId.isBlank()) {
                avatar.setSrc("/profiles/" + photoId);
            } else {
                avatar.setSrc("/images/avatar.png");
            }
        }
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        String currentPath = event.getLocation().getPath();
        String currentFirstSegment = event.getLocation().getFirstSegment();

        linkMap.forEach((targetClass, link) -> {
            String href = link.getHref() == null ? "" : link.getHref().replace("/", "");

            boolean active =
                    currentPath.isEmpty() && targetClass.equals(HomeView.class)
                            || (!currentFirstSegment.isEmpty() && currentFirstSegment.equals(href));

            if (active) {
                link.getStyle()
                        .set("background", "#2563eb")
                        .set("color", "white")
                        .set("box-shadow", "0 4px 16px rgba(37,99,235,0.35)");
            } else {
                link.getStyle()
                        .set("background", "transparent")
                        .set("color", "#94a3b8")
                        .set("box-shadow", "none");
            }
        });
    }
}