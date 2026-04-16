package com.example.controller;

import com.example.config.SpringConfig;
import com.example.repository.ProfileRepository;
import com.example.service.AuthService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "/login")
@PageTitle("Login Page")
@AnonymousAllowed
public class LoginView extends VerticalLayout {

    @Autowired
    private ProfileRepository userRepository;

    @Autowired
    private SpringConfig springConfig;

    private final AuthService authService;

    private TextField phoneField;
    private PasswordField passwordField;

    public LoginView(AuthService authService) {
        this.authService = authService;

        setSizeFull();
        setPadding(false);
        setSpacing(false);
        setMargin(false);
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);


        injectAutofillFix();
        getElement().getClassList().add("login-view");
        getStyle()
                .set("margin", "0")
                .set("padding", "32px 20px")
                .set("width", "100%")
                .set("min-height", "100vh")
                .set("background",
                        "radial-gradient(circle at 20% 20%, rgba(59,130,246,0.15), transparent 38%)," +
                        "radial-gradient(circle at 80% 80%, rgba(99,102,241,0.12), transparent 40%)," +
                        "#060f1f")
                .set("position", "relative")
                .set("overflow-x", "hidden")
                .set("overflow-y", "auto")
                .set("font-family", "Inter, Segoe UI, Arial, sans-serif");

        VerticalLayout wrapper = new VerticalLayout();
        wrapper.setPadding(false);
        wrapper.setSpacing(false);
        wrapper.setMargin(false);
        wrapper.setAlignItems(FlexComponent.Alignment.CENTER);
        wrapper.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        wrapper.setWidthFull();
        wrapper.getStyle()
                .set("max-width", "460px")
                .set("width", "100%");

        VerticalLayout formCard = buildLoginCard();
        wrapper.add(formCard);
        add(wrapper);
    }

    private VerticalLayout buildLoginCard() {
        VerticalLayout card = new VerticalLayout();
        card.setPadding(false);
        card.setSpacing(false);
        card.setMargin(false);
        card.setAlignItems(FlexComponent.Alignment.STRETCH);
        card.getStyle()
                .set("width", "100%")
                .set("padding", "38px 34px 30px")
                .set("gap", "16px")
                .set("border-radius", "24px")
                .set("background", "linear-gradient(145deg, rgba(10,23,48,0.82), rgba(6,15,31,0.72))")
                .set("border", "1px solid rgba(96,165,250,0.14)")
                .set("backdrop-filter", "blur(22px)")
                .set("-webkit-backdrop-filter", "blur(22px)")
                .set("box-shadow", "0 22px 60px rgba(0,0,0,0.36)")
                .set("position", "relative");

        Div logoIcon = new Div();
        logoIcon.getStyle()
                .set("width", "50px")
                .set("height", "50px")
                .set("border-radius", "14px")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("background", "linear-gradient(135deg, #1d4ed8 0%, #3b82f6 100%)")
                .set("box-shadow", "0 10px 28px rgba(37,99,235,0.34)")
                .set("margin", "0 auto 10px auto");
        logoIcon.getElement().setProperty("innerHTML",
                "<svg width='22' height='22' viewBox='0 0 24 24' fill='none' stroke='white' stroke-width='2'>" +
                        "<rect x='3' y='11' width='18' height='11' rx='2'/>" +
                        "<path d='M7 11V7a5 5 0 0 1 10 0v4'/>" +
                        "</svg>");

        H2 title = new H2("Tizimga kirish");
        title.getStyle()
                .set("margin", "0")
                .set("text-align", "center")
                .set("color", "#eff6ff")
                .set("font-size", "30px")
                .set("font-weight", "800")
                .set("letter-spacing", "-0.03em")
                .set("line-height", "1.15");

        Paragraph sub = new Paragraph("Hisobingizga kiring va davom eting.");
        sub.getStyle()
                .set("margin", "0 0 10px 0")
                .set("text-align", "center")
                .set("color", "#93b4e8")
                .set("font-size", "14px")
                .set("line-height", "1.8");

        phoneField = new TextField("Email");
        phoneField.setPlaceholder("exsample@gmail.com");
        phoneField.setRequired(true);
        phoneField.setWidthFull();
        phoneField.getElement().setAttribute("autocomplete", "off");
        applyFieldStyles(phoneField);
        phoneField.getElement().setAttribute("autocomplete", "off");
        passwordField = new PasswordField("Parol");
        passwordField.setPlaceholder("Parolingizni kiriting");
        passwordField.setRequired(true);
        passwordField.setWidthFull();
        passwordField.getElement().setAttribute("autocomplete", "off");
        passwordField.getElement().setAttribute("autocomplete", "new-password");
        applyFieldStyles(passwordField);

        Button forgotPassword = new Button("Forgot password?");
        forgotPassword.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        forgotPassword.getStyle()
                .set("align-self", "flex-end")
                .set("padding", "0")
                .set("margin-top", "-6px")
                .set("font-size", "13px")
                .set("font-weight", "600")
                .set("color", "#60a5fa")
                .set("cursor", "pointer");
        forgotPassword.addClickListener(e ->
                Notification.show("Forgot password bosildi")
        );

        Button loginBtn = new Button("Kirish");
        loginBtn.setWidthFull();
        loginBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        loginBtn.getStyle()
                .set("height", "40px")
                .set("border", "none")
                .set("border-radius", "14px")
                .set("background", "linear-gradient(135deg,#0f3d91 0%, #1d4ed8 55%, #2563eb 100%)")
                .set("color", "white")
                .set("font-size", "16px")
                .set("font-weight", "800")
                .set("letter-spacing", "0.01em")
                .set("cursor", "pointer")
                .set("margin-top", "4px")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center");

        loginBtn.getElement().executeJs(
                "this.addEventListener('mouseenter',()=>{" +
                        "this.style.opacity='0.9';" +
                        "this.style.transform='translateY(-1px)';" +
                        "});" +
                        "this.addEventListener('mouseleave',()=>{" +
                        "this.style.opacity='1';" +
                        "this.style.transform='translateY(0)';" +
                        "});"
        );

        loginBtn.addClickListener(e -> handleLogin());

        Div divider = buildDivider();

        Image googleImage = new Image("/images/geogle.png", "Google");
        googleImage.getStyle()
                .set("width", "18px")
                .set("height", "18px");

        Button googleBtn = new Button("Sign in with Google", googleImage);
        googleBtn.setWidthFull();
        googleBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        googleBtn.getStyle()
                .set("height", "40px")
                .set("background", "rgba(255,255,255,0.96)")
                .set("border", "1px solid rgba(15,23,42,0.12)")
                .set("border-radius", "12px")
                .set("color", "#3c4043")
                .set("font-size", "14px")
                .set("font-weight", "600")
                .set("box-shadow", "0 1px 2px rgba(0,0,0,0.12)")
                .set("cursor", "pointer")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("gap", "8px");

        Div registerWrap = new Div();
        registerWrap.getStyle()
                .set("display", "flex")
                .set("justify-content", "center")
                .set("align-items", "center")
                .set("gap", "6px")
                .set("margin-top", "2px")
                .set("flex-wrap", "wrap");

        Span registerText = new Span("Hisobingiz yo‘qmi?");
        registerText.getStyle()
                .set("font-size", "13px")
                .set("color", "#8ea8cf");

        Button registerBtn = new Button("Ro'yxatdan o'tish", e ->
                getUI().ifPresent(ui -> ui.navigate("registration"))
        );
        registerBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        registerBtn.getStyle()
                .set("padding", "0")
                .set("margin", "0")
                .set("font-size", "13px")
                .set("font-weight", "700")
                .set("color", "#60a5fa")
                .set("cursor", "pointer");

        registerWrap.add(registerText, registerBtn);

        card.add(
                logoIcon,
                title,
                sub,
                phoneField,
                passwordField,
                forgotPassword,
                loginBtn,
                divider,
                googleBtn,
                registerWrap
        );

        return card;
    }

    private Div buildDivider() {
        Div wrap = new Div();
        wrap.getStyle()
                .set("display", "flex")
                .set("align-items", "center")
                .set("gap", "12px")
                .set("margin", "6px 0 2px");

        Div left = new Div();
        left.getStyle()
                .set("flex", "1")
                .set("height", "1px")
                .set("background", "rgba(99,140,210,0.12)");

        Span label = new Span("yoki");
        label.getStyle()
                .set("font-size", "12px")
                .set("color", "rgba(130,160,210,0.40)");

        Div right = new Div();
        right.getStyle()
                .set("flex", "1")
                .set("height", "1px")
                .set("background", "rgba(99,140,210,0.12)");

        wrap.add(left, label, right);
        return wrap;
    }

    private void applyFieldStyles(TextField field) {
        field.getStyle()
                .set("--vaadin-input-field-background", "rgba(8,20,42,0.95)")
                .set("--vaadin-input-field-value-color", "#eaf2ff")
                .set("--vaadin-input-field-label-color", "#8fb3e8")
                .set("--vaadin-input-field-placeholder-color", "rgba(143,179,232,0.45)")
                .set("--vaadin-input-field-border-color", "rgba(96,165,250,0.14)")
                .set("--vaadin-input-field-focused-border-color", "rgba(96,165,250,0.45)");
    }

    private void applyFieldStyles(PasswordField field) {
        field.getStyle()
                .set("--vaadin-input-field-background", "rgba(8,20,42,0.95)")
                .set("--vaadin-input-field-value-color", "#eaf2ff")
                .set("--vaadin-input-field-label-color", "#8fb3e8")
                .set("--vaadin-input-field-placeholder-color", "rgba(143,179,232,0.45)")
                .set("--vaadin-input-field-border-color", "rgba(96,165,250,0.14)")
                .set("--vaadin-input-field-focused-border-color", "rgba(96,165,250,0.45)");
    }

    private void injectAutofillFix() {
        getElement().executeJs("""
        if (!document.getElementById('login-autofill-fix')) {
            const style = document.createElement('style');
            style.id = 'login-autofill-fix';
            style.innerHTML = `
                .login-view input:-webkit-autofill,
                .login-view input:-webkit-autofill:hover,
                .login-view input:-webkit-autofill:focus {
                    -webkit-box-shadow: 0 0 0 1000px rgba(8,20,42,0.95) inset !important;
                    -webkit-text-fill-color: #eaf2ff !important;
                    caret-color: #eaf2ff !important;
                    transition: background-color 9999s ease-in-out 0s;
                }

                .login-view vaadin-password-field::part(input-field),
                .login-view vaadin-text-field::part(input-field) {
                    background: rgba(8,20,42,0.95) !important;
                    border-radius: 12px;
                }

                .login-view vaadin-password-field::part(reveal-button) {
                    background: transparent !important;
                    color: #94a3b8 !important;
                }
            `;
            document.head.appendChild(style);
        }
    """);
    }

    private void handleLogin() {
        String phone = phoneField.getValue();
        String password = passwordField.getValue();
        if (phone == null || phone.isBlank() || password == null || password.isBlank()) {
            Notification.show(
                    "Iltimos, barcha maydonlarni to‘ldiring",
                    3000,
                    Notification.Position.TOP_END
            ).addThemeVariants(NotificationVariant.LUMO_CONTRAST);
            return;
        }
        boolean success = authService.login(phone, password);
        if (success) {
            Notification.show(
                    "Muvaffaqiyatli login",
                    3000,
                    Notification.Position.TOP_END
            ).addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            UI.getCurrent().navigate("");
            return;
        }
        Notification.show(
                "Telefon yoki parol noto‘g‘ri",
                3000,
                Notification.Position.TOP_END
        ).addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}