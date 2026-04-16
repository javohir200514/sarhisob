package com.example.controller;

import com.example.companent.MainLayout;
import com.example.dto.ByteArrayMultipartFile;
import com.example.dto.PersonalInfoDTO;
import com.example.service.AttachService;
import com.example.service.ProfileSettingService;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.streams.UploadHandler;

@Route(value = "account-settings", layout = MainLayout.class)
@PageTitle("Profil sozlamalari")
public class AccountSettingsView extends VerticalLayout implements BeforeEnterObserver {

    private final ProfileSettingService profileSettingService;
    private final AttachService attachService;

    private TextField firstNameField;
    private TextField lastNameField;
    private EmailField emailField;

    private PasswordField currentPassField;
    private PasswordField newPassField;
    private PasswordField confirmPassField;

    private Image avatar;
    private Upload avatarUpload;

    public AccountSettingsView(ProfileSettingService profileSettingService,
                               AttachService attachService) {
        this.profileSettingService = profileSettingService;
        this.attachService = attachService;

        configureView();
        add(createCardLayout());
        loadPersonalInfo();
    }

    private void configureView() {
        setWidthFull();
        setPadding(true);
        setSpacing(false);
        setAlignItems(Alignment.CENTER);

        getStyle()
                .set("background", "linear-gradient(180deg, #f8fbff 0%, #eef5ff 100%)")
                .set("min-height", "100vh");
    }

    private VerticalLayout createCardLayout() {
        VerticalLayout card = new VerticalLayout();
        card.setWidth("860px");
        card.setPadding(true);
        card.setSpacing(true);

        card.getStyle()
                .set("background", "#ffffff")
                .set("border-radius", "20px")
                .set("box-shadow", "0 10px 35px rgba(15, 23, 42, 0.08)")
                .set("margin-top", "28px")
                .set("margin-bottom", "28px")
                .set("padding", "34px");

        card.add(
                createTitleSection(),
                createProfilePictureSection(),
                createPersonalInfoSection(),
                createPasswordSection()
        );

        return card;
    }

    private VerticalLayout createTitleSection() {
        VerticalLayout titleLayout = new VerticalLayout();
        titleLayout.setPadding(false);
        titleLayout.setSpacing(false);

        H2 title = new H2("Profil sozlamalari");
        title.getStyle()
                .set("margin", "0")
                .set("font-size", "30px")
                .set("font-weight", "800")
                .set("color", "#0f172a");

        Span subtitle = new Span("Shaxsiy ma'lumotlar, profil rasmi va xavfsizlik sozlamalarini boshqaring.");
        subtitle.getStyle()
                .set("color", "#64748b")
                .set("font-size", "14px")
                .set("margin-top", "6px");

        titleLayout.add(title, subtitle);
        return titleLayout;
    }

    private VerticalLayout createProfilePictureSection() {
        H2 profileTitle = new H2("Profil rasmi");
        profileTitle.getStyle()
                .set("font-size", "18px")
                .set("margin", "18px 0 0 0")
                .set("color", "#1e293b");

        avatar = new Image("/images/avatar.png", "Foydalanuvchi rasmi");
        avatar.setWidth("150px");
        avatar.setHeight("150px");
        avatar.getStyle()
                .set("border-radius", "50%")
                .set("object-fit", "cover")
                .set("border", "4px solid #e2e8f0")
                .set("box-shadow", "0 8px 24px rgba(15, 23, 42, 0.12)");

        try {
            String photoId = profileSettingService.getCurrentPhotoId();
            if (photoId != null && !photoId.isBlank()) {
                avatar.setSrc("/profiles/" + photoId);
            }
        } catch (Exception ignored) {
        }

        avatarUpload = createUploadComponent();

        Button deleteBtn = new Button("Rasmni o‘chirish");
        deleteBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteBtn.getStyle()
                .set("border-radius", "10px")
                .set("font-weight", "600");

        deleteBtn.addClickListener(e -> {
            try {
                String photoId = profileSettingService.getCurrentPhotoId();

                if (photoId == null || photoId.isBlank()) {
                    notifyError("Rasm topilmadi");
                    clearUploadSelection();
                    return;
                }

                profileSettingService.removePhotoId();
                attachService.delete(photoId);

                avatar.setSrc("/images/avatar.png");
                clearUploadSelection();
                refreshMainLayout();

                notifySuccess("Rasm o‘chirildi");
            } catch (Exception ex) {
                notifyError("Rasmni o‘chirishda xatolik yuz berdi");
            }
        });

        Span changeText = new Span("Profil rasmini yangilash");
        changeText.getStyle()
                .set("font-weight", "700")
                .set("font-size", "15px")
                .set("color", "#0f172a");

        Paragraph infoText = new Paragraph("PNG yoki JPG formatdagi rasm yuklang. Maksimal hajm: 5 MB.");
        infoText.getStyle()
                .set("margin", "0")
                .set("color", "#64748b")
                .set("font-size", "14px");

        VerticalLayout uploadColumn = new VerticalLayout(avatarUpload, deleteBtn);
        uploadColumn.setPadding(false);
        uploadColumn.setSpacing(true);
        uploadColumn.setAlignItems(Alignment.START);

        VerticalLayout rightLayout = new VerticalLayout(changeText, infoText, uploadColumn);
        rightLayout.setPadding(false);
        rightLayout.setSpacing(true);
        rightLayout.setAlignItems(Alignment.START);
        rightLayout.setWidthFull();

        HorizontalLayout profileLayout = new HorizontalLayout(avatar, rightLayout);
        profileLayout.setSpacing(true);
        profileLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        profileLayout.setWidthFull();
        profileLayout.getStyle()
                .set("padding", "16px")
                .set("background", "#f8fafc")
                .set("border-radius", "16px")
                .set("border", "1px solid #e2e8f0");

        VerticalLayout section = new VerticalLayout(profileTitle, profileLayout);
        section.setPadding(false);
        section.setSpacing(true);

        return section;
    }

    private Upload createUploadComponent() {
        Upload upload = new Upload(UploadHandler.inMemory((metadata, data) -> {
            try {
                var multipartFile = new ByteArrayMultipartFile(
                        data,
                        metadata.fileName(),
                        metadata.contentType()
                );

                var dto = attachService.upload(multipartFile);
                profileSettingService.replacePhotoId(dto.getId());

                getUI().ifPresent(ui -> ui.access(() -> {
                    avatar.setSrc("/profiles/" + dto.getId());
                    clearUploadSelection();
                    refreshMainLayout();
                }));

                refreshMainLayout();
                notifySuccess("Rasm muvaffaqiyatli yuklandi");

            } catch (Exception e) {
                avatar.setSrc("/images/avatar.png");
                e.printStackTrace();
                clearUploadSelection();
                notifyError("Rasm yuklashda xatolik: " + e.getMessage());

            }
        }));

        upload.setAcceptedFileTypes("image/png", "image/jpeg", "image/jpg");
        upload.setMaxFiles(1);
        upload.setMaxFileSize(5 * 1024 * 1024);
        upload.setDropAllowed(false);
        upload.setWidth("260px");

        upload.addFileRejectedListener(event ->
                notifyError("Faqat PNG yoki JPG rasm yuklash mumkin")
        );

        return upload;
    }

    private VerticalLayout createPersonalInfoSection() {
        H2 personalInfoTitle = new H2("Shaxsiy ma'lumotlar");
        personalInfoTitle.getStyle()
                .set("font-size", "18px")
                .set("margin", "20px 0 0 0")
                .set("color", "#1e293b");

        firstNameField = new TextField("Ism");
        firstNameField.setWidthFull();

        lastNameField = new TextField("Familiya");
        lastNameField.setWidthFull();

        Button saveNameBtn = createPrimaryButton("Saqlash");
        saveNameBtn.addClickListener(e -> {
            try {
                profileSettingService.saveNameAndSurname(
                        firstNameField.getValue(),
                        lastNameField.getValue()
                );

                notifySuccess("Ism va familiya saqlandi");
                refreshMainLayout();
            } catch (Exception ex) {
                notifyError(ex.getMessage());
            }
        });

        HorizontalLayout namesRow = new HorizontalLayout(firstNameField, lastNameField, saveNameBtn);
        namesRow.setWidthFull();
        namesRow.setAlignItems(Alignment.END);
        namesRow.setFlexGrow(1, firstNameField);
        namesRow.setFlexGrow(1, lastNameField);

        emailField = new EmailField("Login yoki email");
        emailField.setWidthFull();

        Button saveEmailBtn = createPrimaryButton("Saqlash");
        saveEmailBtn.addClickListener(e -> {
            try {
                profileSettingService.saveUsername(emailField.getValue());
                notifySuccess("Email muvaffaqiyatli saqlandi");
                refreshMainLayout();
            } catch (Exception ex) {
                notifyError(ex.getMessage());
            }
        });

        HorizontalLayout emailRow = new HorizontalLayout(emailField, saveEmailBtn);
        emailRow.setWidthFull();
        emailRow.setAlignItems(Alignment.END);
        emailRow.setFlexGrow(1, emailField);

        VerticalLayout contentBox = new VerticalLayout(namesRow, emailRow);
        contentBox.setPadding(true);
        contentBox.setSpacing(true);
        contentBox.getStyle()
                .set("background", "#f8fafc")
                .set("border-radius", "16px")
                .set("border", "1px solid #e2e8f0");

        VerticalLayout section = new VerticalLayout(personalInfoTitle, contentBox);
        section.setPadding(false);
        section.setSpacing(true);

        return section;
    }

    private VerticalLayout createPasswordSection() {
        H2 passwordTitle = new H2("Parolni o‘zgartirish");
        passwordTitle.getStyle()
                .set("font-size", "18px")
                .set("margin", "20px 0 0 0")
                .set("color", "#1e293b");

        currentPassField = new PasswordField("Joriy parol");
        currentPassField.setWidthFull();

        newPassField = new PasswordField("Yangi parol");
        newPassField.setWidthFull();

        confirmPassField = new PasswordField("Yangi parolni tasdiqlang");
        confirmPassField.setWidthFull();

        currentPassField.getElement().setAttribute("autocomplete", "new-password");
        newPassField.getElement().setAttribute("autocomplete", "new-password");
        confirmPassField.getElement().setAttribute("autocomplete", "new-password");

        HorizontalLayout passLayout = new HorizontalLayout(newPassField, confirmPassField);
        passLayout.setWidthFull();
        passLayout.setFlexGrow(1, newPassField);
        passLayout.setFlexGrow(1, confirmPassField);

        Button savePasswordBtn = createPrimaryButton("Saqlash");
        savePasswordBtn.addClickListener(e -> savePassword());

        HorizontalLayout saveRow = new HorizontalLayout(savePasswordBtn);
        saveRow.setWidthFull();
        saveRow.setJustifyContentMode(JustifyContentMode.END);

        VerticalLayout contentBox = new VerticalLayout(currentPassField, passLayout, saveRow);
        contentBox.setPadding(true);
        contentBox.setSpacing(true);
        contentBox.getStyle()
                .set("background", "#f8fafc")
                .set("border-radius", "16px")
                .set("border", "1px solid #e2e8f0");

        VerticalLayout section = new VerticalLayout(passwordTitle, contentBox);
        section.setPadding(false);
        section.setSpacing(true);

        return section;
    }

    private Button createPrimaryButton(String text) {
        Button button = new Button(text);
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.getStyle()
                .set("background", "linear-gradient(135deg, rgb(79, 70, 229) 0%, rgb(42, 39, 115) 55%, rgb(16, 185, 129) 100%)")
                .set("border-radius", "10px")
                .set("font-weight", "700")
                .set("padding", "10px 18px")
                .set("box-shadow", "0 8px 18px rgba(79, 70, 229, 0.22)");
        return button;
    }

    private void loadPersonalInfo() {
        try {
            PersonalInfoDTO dto = profileSettingService.getCurrentPersonalInfo();

            firstNameField.setValue(dto.getName() == null ? "" : dto.getName());
            lastNameField.setValue(dto.getSurname() == null ? "" : dto.getSurname());
            emailField.setValue(dto.getUsername() == null ? "" : dto.getUsername());

            if (dto.getPhotoId() != null && !dto.getPhotoId().isBlank()) {
                avatar.setSrc("/profiles/" + dto.getPhotoId());
            } else {
                avatar.setSrc("/images/avatar.png");
            }
        } catch (Exception e) {
            notifyError(e.getMessage());
        }
    }

    private void savePassword() {
        try {
            String current = currentPassField.getValue();
            String newPass = newPassField.getValue();
            String confirm = confirmPassField.getValue();

            if (current == null || current.isBlank()) {
                throw new RuntimeException("Joriy parolni kiriting");
            }
            if (newPass == null || newPass.isBlank()) {
                throw new RuntimeException("Yangi parolni kiriting");
            }
            if (confirm == null || confirm.isBlank()) {
                throw new RuntimeException("Parol tasdiqini kiriting");
            }
            if (!newPass.equals(confirm)) {
                throw new RuntimeException("Yangi parollar bir xil emas");
            }

            profileSettingService.changePassword(current, newPass);

            clearPasswordFields();
            notifySuccess("Parol muvaffaqiyatli o‘zgartirildi");
        } catch (Exception ex) {
            notifyError(ex.getMessage());
        }
    }

    private void refreshMainLayout() {
        getUI().ifPresent(ui -> {
            if (ui.getChildren().findFirst().orElse(null) instanceof MainLayout mainLayout) {
                mainLayout.refreshUserInfo();
            } else {
                ui.getPage().reload();
            }
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        clearPasswordFields();
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        clearPasswordFields();
    }

    private void clearPasswordFields() {
        if (currentPassField != null) {
            currentPassField.clear();
        }
        if (newPassField != null) {
            newPassField.clear();
        }
        if (confirmPassField != null) {
            confirmPassField.clear();
        }
    }

    private void clearUploadSelection() {
        if (avatarUpload != null) {
            avatarUpload.clearFileList();
        }
    }

    private void notifySuccess(String msg) {
        Notification notification = Notification.show(msg, 2500, Notification.Position.TOP_END);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void notifyError(String msg) {
        Notification notification = Notification.show(
                msg == null ? "Serverda xatolik yuz berdi" : msg,
                3000,
                Notification.Position.TOP_END
        );
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}