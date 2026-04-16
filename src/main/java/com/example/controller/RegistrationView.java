package com.example.controller;

import com.example.dto.RegistrationDTO;
import com.example.entity.SmsHistoryEntity;
import com.example.exseption.AppBadException;
import com.example.repository.ProfileRepository;
import com.example.service.AuthService;
import com.example.service.EmailSenderService;
import com.example.service.SmsHistoryService;
import com.example.service.UserService;
import com.vaadin.flow.component.HasValidation;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.LocalDateTime;

@Route("/registration")
@PageTitle("Ro'yxatdan o'tish")
@AnonymousAllowed
public class RegistrationView extends VerticalLayout {
    @Autowired
    private EmailSenderService emailSenderService;

    @Autowired
    private SmsHistoryService smsHistoryService;

    @Autowired
    private AuthService authService;
    @Autowired
    private UserService userService;

    @Autowired
    private ProfileRepository userRepository;


    private TextField firstNameField;
    private TextField lastNameField;
    private EmailField emailField;
    private PasswordField passwordField;
    private PasswordField confirmPasswordField;

    private Span firstNameLabel;
    private Span lastNameLabel;
    private Span emailLabel;
    private Span passwordLabel;
    private Span confirmPasswordLabel;

    public RegistrationView() {
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        setMargin(false);
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        getElement().getClassList().add("registration-view");

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

        injectAutofillFix();

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

        wrapper.add(buildRegistrationCard());
        add(wrapper);
    }

    private void injectAutofillFix() {
        getElement().executeJs("""
        if (!document.getElementById('registration-autofill-fix')) {
            const style = document.createElement('style');
            style.id = 'registration-autofill-fix';
            style.innerHTML = `
                .registration-view input:-webkit-autofill,
                .registration-view input:-webkit-autofill:hover,
                .registration-view input:-webkit-autofill:focus {
                    -webkit-box-shadow: 0 0 0 1000px rgba(8,20,42,0.95) inset !important;
                    -webkit-text-fill-color: #eaf2ff !important;
                    caret-color: #eaf2ff !important;
                    transition: background-color 9999s ease-in-out 0s;
                }

                .registration-view vaadin-text-field::part(input-field),
                .registration-view vaadin-email-field::part(input-field),
                .registration-view vaadin-password-field::part(input-field) {
                    background: rgba(8,20,42,0.95) !important;
                    border-radius: 12px;
                }

                .registration-view vaadin-password-field::part(reveal-button) {
                    background: transparent !important;
                    color: #94a3b8 !important;
                }
            `;
            document.head.appendChild(style);
        }
    """);
    }

    private VerticalLayout buildRegistrationCard() {
        VerticalLayout card = new VerticalLayout();
        card.setPadding(false);
        card.setSpacing(false);
        card.setMargin(false);
        card.setAlignItems(FlexComponent.Alignment.STRETCH);
        card.getStyle()
                .set("width", "100%")
                .set("padding", "34px 30px 28px")
                .set("gap", "14px")
                .set("border-radius", "24px")
                .set("background", "linear-gradient(145deg, rgba(10,23,48,0.82), rgba(6,15,31,0.72))")
                .set("border", "1px solid rgba(96,165,250,0.14)")
                .set("backdrop-filter", "blur(22px)")
                .set("-webkit-backdrop-filter", "blur(22px)")
                .set("box-shadow", "0 22px 60px rgba(0,0,0,0.36)")
                .set("position", "relative");

        H2 title = new H2("Ro'yxatdan o'tish");
        title.getStyle()
                .set("margin", "0")
                .set("text-align", "center")
                .set("color", "#eff6ff")
                .set("font-size", "30px")
                .set("font-weight", "800")
                .set("letter-spacing", "-0.03em")
                .set("line-height", "1.15");

        Paragraph sub = new Paragraph("Yangi hisob yaratish uchun maydonlarni to‘ldiring.");
        sub.getStyle()
                .set("margin", "0 0 8px 0")
                .set("text-align", "center")
                .set("color", "#93b4e8")
                .set("font-size", "14px")
                .set("line-height", "1.8");

        firstNameLabel = createFieldLabel("Ism", true);
        firstNameField = createTextField("Ismingizni kiriting");

        lastNameLabel = createFieldLabel("Familiya", true);
        lastNameField = createTextField("Familiyangizni kiriting");

        emailLabel = createFieldLabel("Email", true);
        emailField = createEmailField("example@gmail.com");

        passwordLabel = createFieldLabel("Parol", true);
        passwordField = createPasswordField("Kamida 6 ta belgi");

        confirmPasswordLabel = createFieldLabel("Parolni tasdiqlash", true);
        confirmPasswordField = createPasswordField("Parolni qayta kiriting");

        Button registerBtn = new Button("Hisob yaratish");
        registerBtn.setWidthFull();
        registerBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        registerBtn.getStyle()
                .set("height", "44px")
                .set("border", "none")
                .set("border-radius", "14px")
                .set("background", "linear-gradient(135deg,#0f3d91 0%, #1d4ed8 55%, #2563eb 100%)")
                .set("color", "white")
                .set("font-size", "15px")
                .set("font-weight", "800")
                .set("letter-spacing", "0.01em")
                .set("cursor", "pointer")
                .set("margin-top", "6px")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center");

        registerBtn.getElement().executeJs("""
            this.addEventListener('mouseenter',()=>{
                this.style.transform='translateY(-1px)';
                this.style.boxShadow='0 16px 36px rgba(37,99,235,0.34)';
            });
            this.addEventListener('mouseleave',()=>{
                this.style.transform='translateY(0)';
                this.style.boxShadow='none';
            });
        """);

        registerBtn.addClickListener(e -> handleSubmit());

        Button loginBtn = new Button("Tizimga kirish", e ->
                getUI().ifPresent(ui -> ui.navigate("login"))
        );
        loginBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        loginBtn.getStyle()
                .set("align-self", "center")
                .set("padding", "0")
                .set("margin-top", "4px")
                .set("font-size", "14px")
                .set("font-weight", "700")
                .set("color", "#60a5fa")
                .set("cursor", "pointer");

        card.add(
                title,
                sub,
                createFieldBlock(firstNameLabel, firstNameField),
                createFieldBlock(lastNameLabel, lastNameField),
                createFieldBlock(emailLabel, emailField),
                createFieldBlock(passwordLabel, passwordField),
                createFieldBlock(confirmPasswordLabel, confirmPasswordField),
                registerBtn,
                loginBtn
        );

        return card;
    }

    private Div createFieldBlock(Span label, com.vaadin.flow.component.Component field) {
        Div block = new Div(label, field);
        block.getStyle()
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("gap", "8px");
        return block;
    }

    private Span createFieldLabel(String text, boolean required) {
        Span label = new Span(text + (required ? " •" : ""));
        label.getStyle()
                .set("font-size", "14px")
                .set("font-weight", "600")
                .set("color", "#8fb3e8");
        return label;
    }

    private TextField createTextField(String placeholder) {
        TextField field = new TextField();
        field.setPlaceholder(placeholder);
        field.setRequiredIndicatorVisible(false);
        field.setWidthFull();
        field.getElement().setAttribute("autocomplete", "off");
        field.setErrorMessage(null);
        applyFieldStyles(field);
        field.addValueChangeListener(e -> clearFieldError(field));
        return field;
    }

    private EmailField createEmailField(String placeholder) {
        EmailField field = new EmailField();
        field.setPlaceholder(placeholder);
        field.setWidthFull();

        field.getElement().setAttribute("autocomplete", "new-email");
        field.getElement().setAttribute("name", "email-" + System.nanoTime());

        applyFieldStyles(field);
        field.addValueChangeListener(e -> clearFieldError(field));
        return field;
    }

    private PasswordField createPasswordField(String placeholder) {
        PasswordField field = new PasswordField();
        field.setPlaceholder(placeholder);
        field.setWidthFull();

        field.getElement().setAttribute("autocomplete", "new-password");
        field.getElement().setAttribute("name", "password-" + System.nanoTime());

        applyFieldStyles(field);
        field.addValueChangeListener(e -> clearFieldError(field));
        return field;
    }

    private void applyFieldStyles(com.vaadin.flow.component.Component field) {
        field.getElement().getStyle()
                .set("--vaadin-input-field-background", "rgba(8,20,42,0.95)")
                .set("--vaadin-input-field-value-color", "#eaf2ff")
                .set("--vaadin-input-field-label-color", "#8fb3e8")
                .set("--vaadin-input-field-placeholder-color", "rgba(143,179,232,0.45)")
                .set("--vaadin-input-field-border-color", "rgba(96,165,250,0.14)")
                .set("--vaadin-input-field-focused-border-color", "rgba(96,165,250,0.45)")
                .set("color", "#eaf2ff");
    }

    private void handleSubmit() {
        boolean valid = true;

        clearAllLabels();
        clearAllInvalids();

        String firstName = value(firstNameField);
        String lastName = value(lastNameField);
        String email = value(emailField);
        String password = value(passwordField);
        String confirmPassword = value(confirmPasswordField);

        if (firstName.isBlank()) {
            setFieldError(firstNameField, firstNameLabel, "Ism — kiritilishi shart");
            valid = false;
        } else if (firstName.length() < 3) {
            setFieldError(firstNameField, firstNameLabel, "Ism — kamida 3 ta belgi");
            valid = false;
        }

        if (lastName.isBlank()) {
            setFieldError(lastNameField, lastNameLabel, "Familiya — kiritilishi shart");
            valid = false;
        }

        if (email.isBlank()) {
            setFieldError(emailField, emailLabel, "Email — kiritilishi shart");
            valid = false;
        } else if (!isValidEmail(email)) {
            setFieldError(emailField, emailLabel, "Email — noto'g'ri format");
            valid = false;
        } else if (userRepository.findByEmailAndVisibleIsTrue(email).isPresent()) {
            setFieldError(emailField, emailLabel, "Email — allaqachon ro'yxatdan o'tgan");
            valid = false;
        }

        if (password.isBlank()) {
            setFieldError(passwordField, passwordLabel, "Parol — kiritilishi shart");
            valid = false;
        } else if (password.length() < 6) {
            setFieldError(passwordField, passwordLabel, "Parol — kamida 6 ta belgi");
            valid = false;
        } else if (!password.matches(".*[A-Za-z].*")) {
            setFieldError(passwordField, passwordLabel, "Parol — kamida 1 ta harf bo'lsin");
            valid = false;
        } else if (!password.matches(".*\\d.*")) {
            setFieldError(passwordField, passwordLabel, "Parol — kamida 1 ta raqam bo'lsin");
            valid = false;
        }

        if (confirmPassword.isBlank()) {
            setFieldError(confirmPasswordField, confirmPasswordLabel, "Tasdiq paroli — kiritilishi shart");
            valid = false;
        } else if (!confirmPassword.equals(password)) {
            setFieldError(confirmPasswordField, confirmPasswordLabel, "Tasdiq paroli — mos emas");
            valid = false;
        }

        if (!valid) {
            Notification notification = Notification.show(
                    "Iltimos, barcha maydonlarni to'g'ri to'ldiring",
                    2500,
                    Notification.Position.TOP_END
            );
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        try {
            RegistrationDTO dto = new RegistrationDTO();
            dto.setFirstName(firstName);
            dto.setLastName(lastName);
            dto.setEmail(email);
            dto.setPassword(password);
            dto.setConfirmPassword(confirmPassword);

            userService.save(dto);
            emailSenderService.sendRegistrationEmail(email);

            Notification notification = Notification.show(
                    "Tasdiqlash kodi emailingizga yuborildi",
                    2500,
                    Notification.Position.TOP_END
            );
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            openVerificationDialog(email);

        } catch (Exception e) {
            Notification notification = Notification.show(
                    "Xatolik yuz berdi: " + e.getMessage(),
                    3000,
                    Notification.Position.TOP_END
            );
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void openVerificationDialog(String email) {
        com.vaadin.flow.component.dialog.Dialog dialog = new com.vaadin.flow.component.dialog.Dialog();
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);

        dialog.addOpenedChangeListener(event -> {
            if (event.isOpened()) {
                dialog.getElement().executeJs("""
                const overlay = this.$.overlay;
                if (!overlay) return;

                const sr = overlay.shadowRoot;
                if (!sr) return;

                const backdrop = sr.querySelector('[part="backdrop"]');
                const overlayPart = sr.querySelector('[part="overlay"]');
                const content = sr.querySelector('[part="content"]');

                if (backdrop) {
                    backdrop.style.background = 'rgba(3, 8, 20, 0.74)';
                    backdrop.style.backdropFilter = 'blur(14px)';
                    backdrop.style.webkitBackdropFilter = 'blur(14px)';
                }

                if (overlayPart) {
                    overlayPart.style.background = 'transparent';
                    overlayPart.style.boxShadow = 'none';
                    overlayPart.style.border = 'none';
                    overlayPart.style.padding = '0';
                }

                if (content) {
                    content.style.background = 'transparent';
                    content.style.boxShadow = 'none';
                    content.style.border = 'none';
                    content.style.padding = '0';
                    content.style.margin = '0';
                    content.style.borderRadius = '28px';
                    content.style.overflow = 'visible';
                }
            """);
            }
        });

        VerticalLayout wrap = new VerticalLayout();
        wrap.setPadding(false);
        wrap.setSpacing(false);
        wrap.setMargin(false);
        wrap.setWidth("460px");
        wrap.getStyle()
                .set("padding", "0")
                .set("margin", "0")
                .set("background",
                        "linear-gradient(180deg, rgba(7,16,34,0.98) 0%, rgba(4,10,24,0.98) 100%)")
                .set("border", "1px solid rgba(96,165,250,0.10)")
                .set("border-radius", "28px")
                .set("overflow", "hidden")
                .set("box-shadow", "0 30px 90px rgba(0,0,0,0.50)")
                .set("position", "relative");

        Div glowTop = new Div();
        glowTop.getStyle()
                .set("position", "absolute")
                .set("top", "-70px")
                .set("right", "-40px")
                .set("width", "180px")
                .set("height", "180px")
                .set("border-radius", "50%")
                .set("background", "radial-gradient(circle, rgba(37,99,235,0.18) 0%, transparent 70%)")
                .set("filter", "blur(18px)")
                .set("pointer-events", "none");

        Div glowBottom = new Div();
        glowBottom.getStyle()
                .set("position", "absolute")
                .set("bottom", "-90px")
                .set("left", "-50px")
                .set("width", "200px")
                .set("height", "200px")
                .set("border-radius", "50%")
                .set("background", "radial-gradient(circle, rgba(29,78,216,0.14) 0%, transparent 72%)")
                .set("filter", "blur(20px)")
                .set("pointer-events", "none");

        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setPadding(false);
        header.setSpacing(false);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.getStyle()
                .set("padding", "18px 20px 14px")
                .set("position", "relative")
                .set("z-index", "2");

        Button backBtn = new Button(VaadinIcon.ARROW_LEFT.create());
        backBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        backBtn.getStyle()
                .set("width", "40px")
                .set("height", "40px")
                .set("min-width", "40px")
                .set("padding", "0")
                .set("border-radius", "14px")
                .set("background", "rgba(255,255,255,0.04)")
                .set("border", "1px solid rgba(255,255,255,0.06)")
                .set("color", "#f8fafc")
                .set("cursor", "pointer");

        H2 title = new H2("Email tasdiqlash");
        title.getStyle()
                .set("margin", "0")
                .set("font-size", "20px")
                .set("font-weight", "800")
                .set("letter-spacing", "-0.02em")
                .set("color", "#f8fafc");

        Div rightSpace = new Div();
        rightSpace.getStyle().set("width", "40px");

        header.add(backBtn, title, rightSpace);

        VerticalLayout body = new VerticalLayout();
        body.setPadding(false);
        body.setSpacing(false);
        body.setMargin(false);
        body.setWidthFull();
        body.getStyle()
                .set("padding", "4px 24px 24px")
                .set("gap", "16px")
                .set("box-sizing", "border-box")
                .set("position", "relative")
                .set("z-index", "2");

        Span badge = new Span("Tasdiqlash kodi yuborildi");
        badge.getStyle()
                .set("align-self", "flex-start")
                .set("padding", "6px 12px")
                .set("border-radius", "999px")
                .set("font-size", "11px")
                .set("font-weight", "700")
                .set("letter-spacing", ".05em")
                .set("text-transform", "uppercase")
                .set("background", "rgba(37,99,235,0.12)")
                .set("border", "1px solid rgba(59,130,246,0.18)")
                .set("color", "#93c5fd");

        Paragraph text = new Paragraph(
                email + " manziliga yuborilgan tasdiqlash kodini kiriting."
        );
        text.getStyle()
                .set("margin", "0")
                .set("font-size", "14px")
                .set("line-height", "1.8")
                .set("color", "#94a3b8");

        TextField codeField = new TextField("Tasdiqlash kodi");
        codeField.setPlaceholder("5 xonali kod");
        codeField.setWidthFull();
        applyFieldStyles(codeField);

        codeField.getStyle()
                .set("--vaadin-input-field-background", "rgba(255,255,255,0.035)")
                .set("--vaadin-input-field-border-color", "rgba(96,165,250,0.10)")
                .set("--vaadin-input-field-focused-border-color", "rgba(59,130,246,0.42)");

        HorizontalLayout timerRow = new HorizontalLayout();
        timerRow.setWidthFull();
        timerRow.setPadding(false);
        timerRow.setSpacing(false);
        timerRow.setAlignItems(FlexComponent.Alignment.CENTER);
        timerRow.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        Span timerHint = new Span("Kod amal qilish vaqti");
        timerHint.getStyle()
                .set("font-size", "12px")
                .set("color", "#64748b");

        Span timerLabel = new Span("02:00");
        timerLabel.getStyle()
                .set("font-size", "14px")
                .set("font-weight", "800")
                .set("color", "#60a5fa");

        timerRow.add(timerHint, timerLabel);

        Button verifyBtn = new Button("Tasdiqlash");
        verifyBtn.setWidthFull();
        verifyBtn.getStyle()
                .set("height", "46px")
                .set("border-radius", "16px")
                .set("background", "linear-gradient(135deg,#163b7a 0%, #1d4ed8 55%, #2563eb 100%)")
                .set("color", "#ffffff")
                .set("font-size", "15px")
                .set("font-weight", "800")
                .set("letter-spacing", "0.01em")
                .set("cursor", "pointer")
                .set("border", "1px solid rgba(96,165,250,0.14)")
                .set("box-shadow", "0 14px 28px rgba(29,78,216,0.22)");

        Button resendBtn = new Button("Kodni qayta yuborish");
        resendBtn.setVisible(false);
        resendBtn.setWidthFull();
        resendBtn.getStyle()
                .set("height", "44px")
                .set("border-radius", "16px")
                .set("border", "1px solid rgba(96,165,250,0.20)")
                .set("background", "rgba(37,99,235,0.08)")
                .set("color", "#93c5fd")
                .set("font-size", "14px")
                .set("font-weight", "700")
                .set("cursor", "pointer");

        backBtn.addClickListener(e -> {
            dialog.close();
            UI.getCurrent().navigate("");
        });

        verifyBtn.addClickListener(e -> {
            String code = codeField.getValue() == null ? "" : codeField.getValue().trim();

            if (code.isBlank()) {
                Notification notification = Notification.show(
                        "Tasdiqlash kodini kiriting",
                        2500,
                        Notification.Position.TOP_END
                );
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            try {
                boolean result = smsHistoryService.isCodeSentToEmail(email, code);

                if (result) {

                    userService.enableByEmail(email);

                    Notification notification = Notification.show(
                            "Email muvaffaqiyatli tasdiqlandi",
                            2500,
                            Notification.Position.TOP_END
                    );
                    notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                    dialog.close();
                    UI.getCurrent().navigate("/login");
                }
            } catch (Exception ex) {
                Notification notification = Notification.show(
                        ex.getMessage(),
                        3000,
                        Notification.Position.TOP_END
                );
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        resendBtn.addClickListener(e -> {
            try {
                emailSenderService.sendRegistrationEmail(email);

                Notification notification = Notification.show(
                        "Kod qayta yuborildi",
                        2500,
                        Notification.Position.TOP_END
                );
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                resendBtn.setVisible(false);
                timerLabel.setVisible(true);
                timerHint.setVisible(true);
                startVerificationTimer(dialog, timerLabel, resendBtn, 120);
            } catch (Exception ex) {
                Notification notification = Notification.show(
                        "Kod yuborishda xatolik: " + ex.getMessage(),
                        3000,
                        Notification.Position.TOP_END
                );
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        body.add(badge, text, codeField, timerRow, verifyBtn, resendBtn);
        wrap.add(glowTop, glowBottom, header, body);

        dialog.add(wrap);
        dialog.open();

        startVerificationTimer(dialog, timerLabel, resendBtn, 120);
    }

    private void startVerificationTimer(
            com.vaadin.flow.component.dialog.Dialog dialog,
            Span timerLabel,
            Button resendBtn,
            int totalSeconds
    ) {
        timerLabel.setVisible(true);
        resendBtn.setVisible(false);

        dialog.getElement().executeJs("""
        const timerEl = $0;
        const resendEl = $1;
        let remaining = $2;

        if (window.registrationOtpTimer) {
            clearInterval(window.registrationOtpTimer);
        }

        function formatTime(sec) {
            const min = String(Math.floor(sec / 60)).padStart(2, '0');
            const secPart = String(sec % 60).padStart(2, '0');
            return min + ':' + secPart;
        }

        timerEl.textContent = formatTime(remaining);

        window.registrationOtpTimer = setInterval(() => {
            remaining--;
            timerEl.textContent = formatTime(remaining);

            if (remaining <= 0) {
                clearInterval(window.registrationOtpTimer);
                timerEl.style.display = 'none';
                resendEl.style.display = 'inline-flex';
                resendEl.style.width = '100%';
            }
        }, 1000);
    """, timerLabel.getElement(), resendBtn.getElement(), totalSeconds);
    }
    public boolean isCodeSentToEmail(String email, String code) {
        SmsHistoryEntity smsHistoryEntity =smsHistoryService.getByEmail(email);

        Integer attemptCount = smsHistoryService.getAttemptCount(email);
        if (attemptCount == null) {
            attemptCount = 0;
        }

        long secondsBetween = Duration.between(
                smsHistoryEntity.getCreatedDate(),
                LocalDateTime.now()
        ).getSeconds();

        if (secondsBetween > 120) {
            throw new AppBadException("Tasdiqlash kodi eskirgan. Qaytadan kod oling.");
        }

        if (attemptCount > 3) {
            throw new AppBadException("Urinishlar soni tugagan. Qaytadan kod oling.");
        }

        if (!code.equals(smsHistoryEntity.getCode())) {
            int remainingAttempts = Math.max(0, 3 - attemptCount);
            throw new AppBadException("Tasdiqlash kodi noto‘g‘ri. Qolgan urinishlar soni: " + remainingAttempts);
        }

        smsHistoryService.deleteById(smsHistoryEntity.getId());
        return true;
    }

    private void clearAllLabels() {
        resetLabel(firstNameLabel, "Ism", true);
        resetLabel(lastNameLabel, "Familiya", true);
        resetLabel(emailLabel, "Email", true);
        resetLabel(passwordLabel, "Parol", true);
        resetLabel(confirmPasswordLabel, "Parolni tasdiqlash", true);
    }

    private void clearAllInvalids() {
        firstNameField.setInvalid(false);
        lastNameField.setInvalid(false);
        emailField.setInvalid(false);
        passwordField.setInvalid(false);
        confirmPasswordField.setInvalid(false);
    }

    private void clearFieldError(HasValidation field) {
        field.setInvalid(false);
        clearAllLabels();
    }

    private void setFieldError(HasValidation field, Span label, String message) {
        field.setInvalid(true);
        label.setText(message);
        label.getStyle()
                .set("color", "#f87171")
                .set("font-size", "13px")
                .set("font-weight", "700");
    }

    private void resetLabel(Span label, String text, boolean required) {
        label.setText(text + (required ? " •" : ""));
        label.getStyle()
                .set("color", "#8fb3e8")
                .set("font-size", "14px")
                .set("font-weight", "600");
    }

    private String value(TextField field) {
        return field.getValue() == null ? "" : field.getValue().trim();
    }

    private String value(EmailField field) {
        return field.getValue() == null ? "" : field.getValue().trim();
    }

    private String value(PasswordField field) {
        return field.getValue() == null ? "" : field.getValue().trim();
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }
}