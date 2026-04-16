package com.example.controller;

import com.example.companent.MainLayout;
import com.example.dto.CategoryDTO;
import com.example.dto.ExpenseDTO;
import com.example.service.ExpenseService;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

@Route(value = "/expenses", layout = MainLayout.class)
public class ExpensesView extends VerticalLayout implements BeforeEnterObserver {

    private final ExpenseService expenseService;

    private final List<ExpenseDTO> allExpenses = new ArrayList<>();
    private List<ExpenseDTO> filteredExpenses = new ArrayList<>();
    private final List<CategoryDTO> categories = new ArrayList<>();

    private H3 totalAmountLabel;
    private Span transactionCountLabel;
    private H3 avgTransactionValueLabel;
    private Span topCategoryLabel;
    private H3 transactionValueLabel;
    private H3 topCategoryValueLabel;

    private Grid<ExpenseDTO> expensesGrid;
    private Div categoriesGridWrap;

    private ComboBox<Integer> yearCombo;
    private ComboBox<Month> monthCombo;
    private Button monthlyBtn;
    private Button yearlyBtn;

    private boolean filterByMonth = true;

    private static final String C_PAGE = "#0b1020";
    private static final String C_CARD = "rgba(15,23,42,0.96)";
    private static final String C_CARD_ALT = "rgba(18,28,48,0.96)";
    private static final String C_TEXT_1 = "#e2e8f0";
    private static final String C_TEXT_2 = "#94a3b8";
    private static final String C_TEXT_3 = "#64748b";
    private static final String C_BORDER = "1px solid rgba(148,163,184,0.12)";
    private static final String C_BORDER_STRONG = "1px solid rgba(148,163,184,0.18)";
    private static final String GRAD_PRIMARY = "linear-gradient(135deg,#2563eb 0%,#3b82f6 100%)";
    private static final String SHADOW_CARD = "0 10px 28px rgba(0,0,0,0.24)";
    private static final String R_MD = "14px";
    private static final String R_LG = "18px";
    private static final String R_XL = "22px";

    public ExpensesView(ExpenseService expenseService) {
        this.expenseService = expenseService;

        setSizeFull();
        setPadding(false);
        setSpacing(false);
        setMargin(false);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        getStyle()
                .set("width", "100%")
                .set("min-height", "100vh")
                .set("background", C_PAGE)
                .set("font-family", "Inter, Segoe UI, Arial, sans-serif");

        initCategories();

        Div page = new Div();
        page.setWidthFull();
        page.getStyle()
                .set("min-height", "100vh")
                .set("background", "linear-gradient(180deg,#0b1020 0%,#0f172a 100%)")
                .set("padding", "34px 0 64px")
                .set("box-sizing", "border-box")
                .set("overflow-x", "hidden");

        Div shell = new Div();
        shell.setWidthFull();
        shell.getStyle()
                .set("width", "100%")
                .set("max-width", "1450px")
                .set("margin", "0 auto")
                .set("padding-left", "80px")
                .set("padding-right", "90px")
                .set("box-sizing", "border-box");

        shell.add(
                createTopBar(),
                createKpiRow(),
                createCategoriesSection(),
                createExpensesTableSection()
        );

        page.add(shell);
        add(page);

        loadFromDb();
    }

    private void loadFromDb() {
        allExpenses.clear();
        try {
            allExpenses.addAll(expenseService.getAllByCurrentUser());
        } catch (Exception ignored) {
        }
        applyFilterAndRefresh();
    }

    private void applyFilterAndRefresh() {
        int year = (yearCombo != null && yearCombo.getValue() != null)
                ? yearCombo.getValue()
                : LocalDate.now().getYear();

        Month month = (monthCombo != null && monthCombo.getValue() != null)
                ? monthCombo.getValue()
                : LocalDate.now().getMonth();

        filteredExpenses = allExpenses.stream()
                .filter(e -> e.getDate() != null && e.getDate().getYear() == year)
                .filter(e -> !filterByMonth || e.getDate().getMonth() == month)
                .collect(Collectors.toList());

        updateKpis();
        updateCategoriesGrid();

        if (expensesGrid != null) {
            expensesGrid.setItems(filteredExpenses);
            expensesGrid.getDataProvider().refreshAll();
        }
    }

    private void updateKpis() {
        BigDecimal total = filteredExpenses.stream()
                .map(ExpenseDTO::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalAmountLabel != null) {
            totalAmountLabel.setText(String.format("%,.0f so‘m", total));
        }

        if (transactionValueLabel != null) {
            transactionValueLabel.setText(String.valueOf(filteredExpenses.size()));
        }

        if (transactionCountLabel != null) {
            transactionCountLabel.setText("Joriy filtr bo‘yicha");
        }

        if (avgTransactionValueLabel != null) {
            BigDecimal avg = filteredExpenses.isEmpty()
                    ? BigDecimal.ZERO
                    : total.divide(BigDecimal.valueOf(filteredExpenses.size()), 0, RoundingMode.HALF_UP);
            avgTransactionValueLabel.setText(String.format("%,.0f so‘m", avg));
        }

        if (topCategoryValueLabel != null) {
            String top = filteredExpenses.stream()
                    .filter(e -> e.getCategory() != null)
                    .collect(Collectors.groupingBy(
                            ExpenseDTO::getCategory,
                            Collectors.reducing(
                                    BigDecimal.ZERO,
                                    e -> e.getAmount() == null ? BigDecimal.ZERO : e.getAmount(),
                                    BigDecimal::add
                            )
                    ))
                    .entrySet()
                    .stream()
                    .max(Comparator.comparing(e -> e.getValue() == null ? BigDecimal.ZERO : e.getValue()))
                    .map(e -> e.getKey())
                    .orElse("—");

            topCategoryValueLabel.setText("—".equals(top) ? "—" : getCategoryLabelUz(top));
        }

        if (topCategoryLabel != null) {
            topCategoryLabel.setText("Eng ko‘p xarajat qilingan bo‘lim");
        }
    }

    private String getCategoryLabelUz(String cat) {
        if (cat == null || cat.isBlank()) return "Boshqa";

        return switch (cat.trim().toLowerCase(Locale.ROOT)) {
            case "education", "ta'lim" -> "Ta'lim";
            case "food", "ovqat" -> "Ovqat";
            case "transport" -> "Transport";
            case "utilities", "kommunal" -> "Kommunal";
            case "rent", "ijara" -> "Ijara";
            case "internet" -> "Internet";
            case "healthcare", "sog‘liq", "sog'liq" -> "Sog‘liq";
            case "entertainment", "ko‘ngilochar", "ko'ngilochar" -> "Ko‘ngilochar";
            case "clothing", "kiyim" -> "Kiyim";
            case "travel", "sayohat" -> "Sayohat";
            case "gifts", "sovg‘a", "sovg'a" -> "Sovg‘a";
            case "investment", "investitsiya" -> "Investitsiya";
            case "savings", "jamg‘arma", "jamg'arma" -> "Jamg‘arma";
            case "tobacco & alcohol", "zararli odatlar" -> "Zararli odatlar";
            default -> "Boshqa";
        };
    }

    private void initCategories() {
        categories.add(new CategoryDTO("Ta'lim", "📘"));
        categories.add(new CategoryDTO("Ovqat", "🍴"));
        categories.add(new CategoryDTO("Transport", "🚗"));
        categories.add(new CategoryDTO("Kommunal", "💡"));
        categories.add(new CategoryDTO("Ijara", "🏠"));
        categories.add(new CategoryDTO("Internet", "🌐"));
        categories.add(new CategoryDTO("Sog‘liq", "🩺"));
        categories.add(new CategoryDTO("Ko‘ngilochar", "🎬"));
        categories.add(new CategoryDTO("Kiyim", "👕"));
        categories.add(new CategoryDTO("Sayohat", "✈️"));
        categories.add(new CategoryDTO("Sovg‘a", "🎁"));
        categories.add(new CategoryDTO("Investitsiya", "📈"));
        categories.add(new CategoryDTO("Jamg‘arma", "💰"));
        categories.add(new CategoryDTO("Zararli odatlar", "🥃"));
        categories.add(new CategoryDTO("Boshqa", "📦"));
    }

    private String getEmojiForCategory(String cat) {
        if (cat == null) return "📦";
        return switch (cat.trim().toLowerCase(Locale.ROOT)) {
            case "ta'lim" -> "📘";
            case "ovqat" -> "🍴";
            case "transport" -> "🚗";
            case "kommunal" -> "💡";
            case "ijara" -> "🏠";
            case "internet" -> "🌐";
            case "sog‘liq" -> "🩺";
            case "ko‘ngilochar" -> "🎬";
            case "kiyim" -> "👕";
            case "sayohat" -> "✈️";
            case "sovg‘a" -> "🎁";
            case "investitsiya" -> "📈";
            case "jamg‘arma" -> "💰";
            case "zararli odatlar" -> "🥃";
            default -> "📦";
        };
    }

    private String getAccentColor(String cat) {
        if (cat == null) return "#64748b";
        return switch (cat.trim().toLowerCase(Locale.ROOT)) {
            case "ovqat" -> "#f97316";
            case "transport" -> "#0ea5e9";
            case "ta'lim" -> "#6366f1";
            case "kommunal" -> "#f59e0b";
            case "ijara" -> "#8b5cf6";
            case "internet" -> "#06b6d4";
            case "sog‘liq" -> "#ef4444";
            case "ko‘ngilochar" -> "#ec4899";
            case "kiyim" -> "#14b8a6";
            case "sayohat" -> "#22c55e";
            case "sovg‘a" -> "#f43f5e";
            case "investitsiya" -> "#10b981";
            case "jamg‘arma" -> "#eab308";
            case "zararli odatlar" -> "#a16207";
            default -> "#64748b";
        };
    }

    private BigDecimal parseAmountSafe(String val) {
        if (val == null) return BigDecimal.ZERO;
        String c = val.trim().replace(" ", "").replace(",", "");
        if (c.isEmpty()) return BigDecimal.ZERO;
        try {
            return new BigDecimal(c);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private Div createTopBar() {
        Div bar = new Div();
        bar.setWidthFull();
        bar.getStyle()
                .set("display", "flex")
                .set("align-items", "flex-end")
                .set("justify-content", "space-between")
                .set("flex-wrap", "wrap")
                .set("gap", "18px")
                .set("margin-bottom", "26px");

        Div left = new Div();
        left.getStyle()
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("gap", "8px");

        H1 title = new H1("Xarajatlar");
        title.getStyle()
                .set("font-size", "clamp(1.8rem,3vw,2.4rem)")
                .set("font-weight", "700")
                .set("letter-spacing", "-0.02em")
                .set("line-height", "1.1")
                .set("color", "#e2e8f0");

        Span sub = new Span("Xarajatlaringizni kuzating va boshqaring.");
        sub.getStyle()
                .set("font-size", "13px")
                .set("color", C_TEXT_3);

        left.add(title, sub);

        Div controls = new Div();
        controls.getStyle()
                .set("display", "flex")
                .set("align-items", "flex-end")
                .set("gap", "10px")
                .set("flex-wrap", "wrap");

        int nowYear = LocalDate.now().getYear();

        yearCombo = new ComboBox<>("Yil");
        yearCombo.setItems(nowYear - 3, nowYear - 2, nowYear - 1, nowYear, nowYear + 1);
        yearCombo.setValue(nowYear);
        yearCombo.setWidth("110px");
        styleCombo(yearCombo);
        yearCombo.addValueChangeListener(e -> applyFilterAndRefresh());

        monthCombo = new ComboBox<>("Oy");
        monthCombo.setItems(Month.values());
        monthCombo.setValue(LocalDate.now().getMonth());
        monthCombo.setItemLabelGenerator(m -> m.getDisplayName(TextStyle.FULL, new Locale("uz")));
        monthCombo.setWidth("150px");
        styleCombo(monthCombo);
        monthCombo.addValueChangeListener(e -> applyFilterAndRefresh());

        Div toggleWrap = new Div();
        toggleWrap.getStyle()
                .set("display", "inline-flex")
                .set("align-items", "center")
                .set("gap", "4px")
                .set("background", C_CARD)
                .set("border", C_BORDER)
                .set("border-radius", R_MD)
                .set("padding", "4px");

        monthlyBtn = new Button("Oylik", e -> {
            filterByMonth = true;
            monthCombo.setVisible(true);
            refreshToggleButtons();
            applyFilterAndRefresh();
        });

        yearlyBtn = new Button("Yillik", e -> {
            filterByMonth = false;
            monthCombo.setVisible(false);
            refreshToggleButtons();
            applyFilterAndRefresh();
        });

        refreshToggleButtons();
        toggleWrap.add(monthlyBtn, yearlyBtn);

        Button addBtn = new Button("Yangi xarajat");
        addBtn.getStyle()
                .set("background", GRAD_PRIMARY)
                .set("color", "white")
                .set("border", "none")
                .set("padding", "0 20px")
                .set("height", "42px")
                .set("border-radius", R_MD)
                .set("font-weight", "700")
                .set("font-size", "13px")
                .set("cursor", "pointer")
                .set("box-shadow", "0 8px 22px rgba(37,99,235,0.24)");

        addBtn.addClickListener(e -> openExpenseDialogNice(null));

        controls.add(yearCombo, monthCombo, toggleWrap, addBtn);
        bar.add(left, controls);
        return bar;
    }

    private void refreshToggleButtons() {
        styleToggle(monthlyBtn, filterByMonth);
        styleToggle(yearlyBtn, !filterByMonth);
    }

    private void styleToggle(Button btn, boolean active) {
        btn.getStyle()
                .set("height", "34px")
                .set("padding", "0 15px")
                .set("border-radius", "10px")
                .set("font-weight", "700")
                .set("font-size", "12px")
                .set("cursor", "pointer")
                .set("border", "none")
                .set("background", active ? "#1d4ed8" : "transparent")
                .set("color", active ? "#ffffff" : C_TEXT_2)
                .set("box-shadow", active ? "0 6px 16px rgba(37,99,235,0.20)" : "none");
    }

    private Div createKpiRow() {
        Div row = new Div();
        row.setWidthFull();
        row.getStyle()
                .set("display", "grid")
                .set("grid-template-columns", "repeat(4,1fr)")
                .set("gap", "14px")
                .set("margin-bottom", "24px");

        Div c1 = buildKpiCard("Jami xarajat", VaadinIcon.WALLET.create());
        totalAmountLabel = getKpiValue(c1);
        getKpiSub(c1).setText("Joriy filtr bo‘yicha");

        Div c2 = buildKpiCard("Xarajatlar soni", VaadinIcon.CLIPBOARD_TEXT.create());
        transactionValueLabel = getKpiValue(c2);
        transactionCountLabel = getKpiSub(c2);
        transactionCountLabel.setText("Joriy filtr bo‘yicha");

        Div c3 = buildKpiCard("O‘rtacha xarajat", VaadinIcon.CHART_LINE.create());
        avgTransactionValueLabel = getKpiValue(c3);
        getKpiSub(c3).setText("Bitta xarajatga");

        Div c4 = buildKpiCard("Asosiy kategoriya", VaadinIcon.PIE_CHART.create());
        topCategoryValueLabel = getKpiValue(c4);
        topCategoryLabel = getKpiSub(c4);
        topCategoryLabel.setText("Joriy filtr bo‘yicha");

        row.add(c1, c2, c3, c4);

        row.getElement().executeJs("""
        const mq = window.matchMedia('(max-width:880px)');
        const el = this;
        function apply(){
            el.style.gridTemplateColumns = mq.matches ? 'repeat(2,1fr)' : 'repeat(4,1fr)';
        }
        apply();
        mq.addEventListener('change',apply);
    """);

        return row;
    }


    private Div buildKpiCard(String label, com.vaadin.flow.component.Component icon) {
        Div card = new Div();
        card.getStyle()
                .set("padding", "20px")
                .set("border-radius", R_XL)
                .set("background", C_CARD)
                .set("border", C_BORDER)
                .set("box-shadow", SHADOW_CARD)
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("gap", "10px");

        Div top = new Div();
        top.getStyle()
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "space-between");

        Span lbl = new Span(label);
        lbl.getStyle()
                .set("font-size", "11px")
                .set("font-weight", "700")
                .set("letter-spacing", ".08em")
                .set("text-transform", "uppercase")
                .set("color", C_TEXT_3);

        Div iconWrap = new Div(icon);
        iconWrap.getStyle()
                .set("width", "38px")
                .set("height", "38px")
                .set("border-radius", "12px")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("background", "rgba(37,99,235,0.10)")
                .set("border", "1px solid rgba(59,130,246,0.16)")
                .set("color", "#60a5fa");

        H3 val = new H3("—");
        val.getStyle()
                .set("margin", "0")
                .set("font-size", "1.6rem")
                .set("font-weight", "900")
                .set("letter-spacing", "-0.04em")
                .set("color", C_TEXT_1);

        Span sub = new Span("—");
        sub.getStyle()
                .set("font-size", "12px")
                .set("color", C_TEXT_2);

        top.add(lbl, iconWrap);
        card.add(top, val, sub);
        return card;
    }

    private H3 getKpiValue(Div card) {
        return card.getChildren()
                .filter(c -> c instanceof H3)
                .map(c -> (H3) c)
                .findFirst()
                .orElse(new H3());
    }

    private Span getKpiSub(Div card) {
        return card.getChildren()
                .filter(c -> c instanceof Span)
                .map(c -> (Span) c)
                .skip(1)
                .findFirst()
                .orElse(new Span());
    }

    private Div createCategoriesSection() {
        Div section = new Div();
        section.setWidthFull();
        section.getStyle().set("margin-bottom", "24px");

        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setPadding(false);
        header.setSpacing(false);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.getStyle().set("margin-bottom", "14px");

        H2 title = new H2("Kategoriyalar");
        title.getStyle()
                .set("margin", "0")
                .set("font-size", "1.15rem")
                .set("font-weight", "800")
                .set("color", C_TEXT_1);

        Span hint = new Span("Kategoriya ustiga bossangiz xarajat qo‘shiladi");
        hint.getStyle()
                .set("font-size", "12px")
                .set("color", C_TEXT_3);

        header.add(title, hint);

        categoriesGridWrap = new Div();
        categoriesGridWrap.setWidthFull();
        categoriesGridWrap.getStyle()
                .set("display", "grid")
                .set("grid-template-columns", "repeat(5,1fr)")
                .set("gap", "10px");

        categoriesGridWrap.getElement().executeJs("""
            const mq1 = window.matchMedia('(max-width:1100px)');
            const mq2 = window.matchMedia('(max-width:700px)');
            const el = this;
            function apply(){
                if(mq2.matches) el.style.gridTemplateColumns='repeat(3,1fr)';
                else if(mq1.matches) el.style.gridTemplateColumns='repeat(4,1fr)';
                else el.style.gridTemplateColumns='repeat(5,1fr)';
            }
            apply();
            mq1.addEventListener('change',apply);
            mq2.addEventListener('change',apply);
        """);

        section.add(header, categoriesGridWrap);
        return section;
    }

    private void updateCategoriesGrid() {
        if (categoriesGridWrap == null) return;
        categoriesGridWrap.removeAll();

        for (CategoryDTO c : categories) {
            BigDecimal total = filteredExpenses.stream()
                    .filter(e -> c.getName().equals(e.getCategory()))
                    .map(ExpenseDTO::getAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            boolean active = total.compareTo(BigDecimal.ZERO) > 0;
            String accent = getAccentColor(c.getName());

            Div card = new Div();
            card.getStyle()
                    .set("padding", "14px 12px")
                    .set("border-radius", R_LG)
                    .set("background", active ? C_CARD_ALT : C_CARD)
                    .set("border", active ? "1px solid " + accent + "33" : C_BORDER)
                    .set("box-shadow", SHADOW_CARD)
                    .set("display", "flex")
                    .set("flex-direction", "column")
                    .set("gap", "12px")
                    .set("cursor", "pointer")
                    .set("transition", "transform .18s ease");

            Div top = new Div();
            top.getStyle()
                    .set("display", "flex")
                    .set("align-items", "center")
                    .set("justify-content", "space-between");

            Div iconWrap = new Div(new Text(c.getIcon()));
            iconWrap.getStyle()
                    .set("width", "36px")
                    .set("height", "36px")
                    .set("border-radius", "12px")
                    .set("display", "flex")
                    .set("align-items", "center")
                    .set("justify-content", "center")
                    .set("background", active ? accent + "20" : "rgba(255,255,255,0.05)")
                    .set("font-size", "18px");

            Span amount = new Span(active ? String.format("%,.0f", total) : "0");
            amount.getStyle()
                    .set("font-size", "12px")
                    .set("font-weight", "700")
                    .set("color", active ? accent : C_TEXT_3);

            Span name = new Span(c.getName());
            name.getStyle()
                    .set("font-size", "13px")
                    .set("font-weight", "700")
                    .set("color", C_TEXT_1);

            card.add(top, name);
            top.add(iconWrap, amount);

            card.getElement().addEventListener("mouseenter", e ->
                    card.getStyle().set("transform", "translateY(-3px)")
            );
            card.getElement().addEventListener("mouseleave", e ->
                    card.getStyle().set("transform", "translateY(0)")
            );

            card.addClickListener(e -> openQuickAddDialog(c));
            categoriesGridWrap.add(card);
        }
    }

    private Div createExpensesTableSection() {
        Div section = new Div();
        section.setWidthFull();
        section.getStyle()
                .set("width", "100%")
                .set("box-sizing", "border-box")
                .set("align-self", "stretch")
                .set("border-radius", R_XL)
                .set("overflow", "hidden")
                .set("background", C_CARD)
                .set("border", C_BORDER)
                .set("box-shadow", SHADOW_CARD)
                .set("margin", "0");

        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setPadding(false);
        header.setSpacing(false);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.getStyle()
                .set("padding", "18px 22px")
                .set("border-bottom", "1px solid rgba(148,163,184,0.08)")
                .set("box-sizing", "border-box")
                .set("width", "100%");

        H2 title = new H2("So‘nggi xarajatlar");
        title.getStyle()
                .set("margin", "0")
                .set("font-size", "1.15rem")
                .set("font-weight", "800")
                .set("color", C_TEXT_1);

        TextField search = new TextField();
        search.setPlaceholder("Qidirish...");
        search.setWidth("220px");
        styleTextField(search);
        search.addValueChangeListener(e -> {
            String q = e.getValue().toLowerCase(Locale.ROOT);
            expensesGrid.setItems(filteredExpenses.stream()
                    .filter(ex -> (ex.getCategory() != null && ex.getCategory().toLowerCase().contains(q))
                            || (ex.getDescription() != null && ex.getDescription().toLowerCase().contains(q)))
                    .collect(Collectors.toList()));
        });

        header.add(title, search);

        expensesGrid = new Grid<>(ExpenseDTO.class, false);
        expensesGrid.setWidthFull();
        expensesGrid.setItems(filteredExpenses);
        expensesGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES);
        styleGrid(expensesGrid);

        expensesGrid.addComponentColumn(ex -> {
            HorizontalLayout cell = new HorizontalLayout();
            cell.setPadding(false);
            cell.setSpacing(false);
            cell.setAlignItems(FlexComponent.Alignment.CENTER);
            cell.getStyle().set("gap", "10px");

            Div iconWrap = new Div(new Text(getEmojiForCategory(ex.getCategory())));
            iconWrap.getStyle()
                    .set("width", "32px")
                    .set("height", "32px")
                    .set("border-radius", "10px")
                    .set("display", "flex")
                    .set("align-items", "center")
                    .set("justify-content", "center")
                    .set("background", "rgba(255,255,255,0.05)");

            Span name = new Span(ex.getCategory() == null ? "Boshqa" : ex.getCategory());
            name.getStyle()
                    .set("font-size", "13px")
                    .set("font-weight", "700")
                    .set("color", C_TEXT_1);

            cell.add(iconWrap, name);
            return cell;
        }).setHeader("Kategoriya").setFlexGrow(1);

        expensesGrid.addComponentColumn(ex -> {
            Span d = new Span(
                    ex.getDescription() == null || ex.getDescription().isBlank()
                            ? "—"
                            : ex.getDescription()
            );
            d.getStyle()
                    .set("font-size", "13px")
                    .set("color", ex.getDescription() == null || ex.getDescription().isBlank() ? C_TEXT_3 : C_TEXT_2);
            return d;
        }).setHeader("Izoh").setFlexGrow(2);

        expensesGrid.addComponentColumn(ex -> {
            BigDecimal amt = ex.getAmount() == null ? BigDecimal.ZERO : ex.getAmount();
            Span amtS = new Span(String.format("%,.0f so‘m", amt));
            amtS.getStyle()
                    .set("font-size", "13px")
                    .set("font-weight", "800")
                    .set("color", C_TEXT_1);
            return amtS;
        }).setHeader("Miqdor").setWidth("160px").setFlexGrow(0);

        expensesGrid.addComponentColumn(ex -> {
            Span d = new Span(
                    ex.getDate() == null
                            ? "—"
                            : ex.getDate().getDayOfMonth() + " " +
                            ex.getDate().getMonth().getDisplayName(TextStyle.SHORT, new Locale("uz")) + " " +
                            ex.getDate().getYear()
            );
            d.getStyle()
                    .set("font-size", "13px")
                    .set("color", C_TEXT_2);
            return d;
        }).setHeader("Sana").setWidth("150px").setFlexGrow(0);

        expensesGrid.addComponentColumn(ex -> {
            HorizontalLayout actions = new HorizontalLayout();
            actions.setPadding(false);
            actions.setSpacing(false);
            actions.getStyle()
                    .set("gap", "8px")
                    .set("justify-content", "flex-end");

            Button editBtn = iconButton(VaadinIcon.EDIT.create(), "#60a5fa", "rgba(96,165,250,0.10)");
            editBtn.addClickListener(e -> openExpenseDialogNice(ex));

            Button delBtn = iconButton(VaadinIcon.TRASH.create(), "#f87171", "rgba(248,113,113,0.10)");
            delBtn.addClickListener(e -> deleteExpense(ex));

            actions.add(editBtn, delBtn);
            return actions;
        }).setHeader("Amallar").setWidth("150px").setFlexGrow(0);

        Div gridWrap = new Div(expensesGrid);
        gridWrap.setWidthFull();
        gridWrap.getStyle()
                .set("width", "100%")
                .set("box-sizing", "border-box");

        section.add(header, gridWrap);
        return section;
    }

    private void styleGrid(Grid<ExpenseDTO> grid) {
        grid.getStyle()
                .set("border", "none")
                .set("background", "transparent")
                .set("color", C_TEXT_1);

        grid.getElement().executeJs("""
            requestAnimationFrame(() => {
                const root = this;
                const sr = root.shadowRoot;
                if (!sr) return;

                const styleId = 'expenses-grid-local-style';
                if (sr.getElementById(styleId)) return;

                const style = document.createElement('style');
                style.id = styleId;
                style.textContent = `
                    [part~="header-cell"] {
                        background: rgba(15,23,42,0.98) !important;
                        color: #64748b !important;
                        font-size: 10px !important;
                        font-weight: 700 !important;
                        letter-spacing: .08em !important;
                        text-transform: uppercase !important;
                        border-bottom: 1px solid rgba(148,163,184,0.08) !important;
                        padding: 14px 18px !important;
                    }

                    [part~="body-cell"] {
                        background: rgba(15,23,42,0.98) !important;
                        color: #cbd5e1 !important;
                        border-bottom: 1px solid rgba(148,163,184,0.05) !important;
                        padding: 12px 18px !important;
                        font-size: 13px !important;
                    }

                    [part~="row"]:hover [part~="body-cell"] {
                        background: rgba(18,28,48,0.98) !important;
                    }

                    table {
                        background: rgba(15,23,42,0.98) !important;
                    }

                    #items {
                        background: rgba(15,23,42,0.98) !important;
                    }

                    [part~="scroller"] {
                        background: rgba(15,23,42,0.98) !important;
                    }
                `;
                sr.appendChild(style);
            });
        """);
    }

    private Button iconButton(Icon icon, String color, String bg) {
        Button btn = new Button(icon);
        btn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        btn.getStyle()
                .set("color", color)
                .set("background", bg)
                .set("border", "1px solid rgba(255,255,255,0.06)")
                .set("border-radius", "10px")
                .set("cursor", "pointer");
        return btn;
    }

    private void deleteExpense(ExpenseDTO expense) {
        Dialog dlg = createStyledDialog();

        VerticalLayout wrap = buildDialogWrap();
        Div header = buildDialogHeader("Xarajatni o‘chirish", dlg);

        VerticalLayout body = buildDialogBody();

        Paragraph text = new Paragraph("Haqiqatan ham bu xarajatni o‘chirmoqchimisiz?");
        text.getStyle()
                .set("margin", "0")
                .set("font-size", "14px")
                .set("line-height", "1.7")
                .set("color", C_TEXT_2);

        Span detail = new Span(
                (expense.getCategory() == null ? "Xarajat" : expense.getCategory()) +
                        " • " +
                        (expense.getAmount() == null ? "0 so‘m" : String.format("%,.0f so‘m", expense.getAmount()))
        );
        detail.getStyle()
                .set("font-size", "13px")
                .set("font-weight", "700")
                .set("color", C_TEXT_1);

        HorizontalLayout actions = buildDialogActions();
        Button cancel = dialogSecondaryButton("Bekor qilish", dlg::close);

        Button delete = new Button("O‘chirish");
        delete.getStyle()
                .set("height", "40px")
                .set("padding", "0 18px")
                .set("border", "none")
                .set("border-radius", R_MD)
                .set("background", "linear-gradient(135deg,#ef4444,#f43f5e)")
                .set("color", "white")
                .set("font-weight", "700")
                .set("cursor", "pointer");

        delete.addClickListener(e -> {
            if (expense.getId() != null) {
                expenseService.delete(expense.getId());
            }
            loadFromDb();
            dlg.close();
            Notification.show("Xarajat o‘chirildi");
        });

        actions.add(cancel, delete);
        body.add(text, detail, actions);
        wrap.add(header, body);

        dlg.add(wrap);
        dlg.open();
    }

    private void openQuickAddDialog(CategoryDTO category) {
        Dialog dlg = createStyledDialog();

        VerticalLayout wrap = buildDialogWrap();
        Div header = buildDialogHeader(getEmojiForCategory(category.getName()) + " " + category.getName(), dlg);

        VerticalLayout body = buildDialogBody();

        TextField amount = styledField("Miqdor", "Masalan 25000");
        TextField desc = styledField("Izoh", "Masalan tushlik");
        DatePicker date = styledDatePicker();

        HorizontalLayout actions = buildDialogActions();
        Button cancel = dialogSecondaryButton("Bekor qilish", dlg::close);

        Button save = new Button("Saqlash");
        save.getStyle()
                .set("height", "40px")
                .set("padding", "0 18px")
                .set("border", "none")
                .set("border-radius", R_MD)
                .set("background", GRAD_PRIMARY)
                .set("color", "white")
                .set("font-weight", "700")
                .set("cursor", "pointer");

        save.addClickListener(e -> {
            BigDecimal a = parseAmountSafe(amount.getValue());
            if (a.compareTo(BigDecimal.ZERO) <= 0) {
                Notification.show("Miqdor 0 dan katta bo‘lishi kerak");
                return;
            }

            expenseService.save(new ExpenseDTO(
                    null,
                    category.getName(),
                    a,
                    date.getValue() == null ? LocalDate.now() : date.getValue(),
                    desc.getValue() == null ? "" : desc.getValue()
            ));

            loadFromDb();
            dlg.close();
        });

        actions.add(cancel, save);
        body.add(amount, desc, date, actions);
        wrap.add(header, body);

        dlg.add(wrap);
        dlg.open();
        amount.focus();
    }

    private void openExpenseDialogNice(ExpenseDTO expense) {
        boolean isEdit = expense != null;

        Dialog dlg = createStyledDialog();

        VerticalLayout wrap = buildDialogWrap();
        Div header = buildDialogHeader(isEdit ? "Xarajatni tahrirlash" : "Yangi xarajat", dlg);
        VerticalLayout body = buildDialogBody();

        ComboBox<CategoryDTO> catBox = new ComboBox<>("Kategoriya");
        catBox.setItems(categories);
        catBox.setItemLabelGenerator(c -> c.getIcon() + " " + c.getName());
        catBox.setWidthFull();
        styleCombo(catBox);

        TextField amount = styledField("Miqdor", "Masalan 25000");
        TextField desc = styledField("Izoh", "Masalan tushlik");
        DatePicker date = styledDatePicker();

        if (isEdit) {
            catBox.setValue(categories.stream()
                    .filter(c -> c.getName().equalsIgnoreCase(expense.getCategory()))
                    .findFirst()
                    .orElse(null));

            amount.setValue(expense.getAmount() == null ? "" : expense.getAmount().toPlainString());
            desc.setValue(expense.getDescription() == null ? "" : expense.getDescription());
            date.setValue(expense.getDate() == null ? LocalDate.now() : expense.getDate());
        }

        HorizontalLayout actions = buildDialogActions();
        Button cancel = dialogSecondaryButton("Bekor qilish", dlg::close);

        Button save = new Button(isEdit ? "Yangilash" : "Saqlash");
        save.getStyle()
                .set("height", "40px")
                .set("padding", "0 18px")
                .set("border", "none")
                .set("border-radius", R_MD)
                .set("background", GRAD_PRIMARY)
                .set("color", "white")
                .set("font-weight", "700")
                .set("cursor", "pointer");

        save.addClickListener(e -> {
            if (catBox.getValue() == null) {
                Notification.show("Kategoriya tanlang");
                return;
            }

            BigDecimal a = parseAmountSafe(amount.getValue());
            if (a.compareTo(BigDecimal.ZERO) <= 0) {
                Notification.show("Miqdor 0 dan katta bo‘lishi kerak");
                return;
            }

            LocalDate d = date.getValue() == null ? LocalDate.now() : date.getValue();
            String dsc = desc.getValue() == null ? "" : desc.getValue();

            if (!isEdit) {
                expenseService.save(new ExpenseDTO(null, catBox.getValue().getName(), a, d, dsc));
            } else {
                expenseService.update(
                        expense.getId(),
                        new ExpenseDTO(expense.getId(), catBox.getValue().getName(), a, d, dsc)
                );
            }

            loadFromDb();
            dlg.close();
        });

        actions.add(cancel, save);
        body.add(catBox, amount, desc, date, actions);
        wrap.add(header, body);

        dlg.add(wrap);
        dlg.open();
        amount.focus();
    }

    private Dialog createStyledDialog() {
        Dialog dlg = new Dialog();
        dlg.setCloseOnEsc(true);
        dlg.setCloseOnOutsideClick(true);

        dlg.addOpenedChangeListener(event -> {
            if (event.isOpened()) {
                dlg.getElement().executeJs("""
                    const overlay = this.$.overlay;
                    if (!overlay) return;

                    const sr = overlay.shadowRoot;
                    if (!sr) return;

                    const backdrop = sr.querySelector('[part="backdrop"]');
                    const overlayPart = sr.querySelector('[part="overlay"]');
                    const content = sr.querySelector('[part="content"]');

                    if (backdrop) {
                        backdrop.style.background = 'rgba(2, 6, 16, 0.58)';
                        backdrop.style.backdropFilter = 'blur(10px)';
                        backdrop.style.webkitBackdropFilter = 'blur(10px)';
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
                        content.style.borderRadius = '26px';
                        content.style.overflow = 'visible';
                    }
                """);
            }
        });

        return dlg;
    }

    private VerticalLayout buildDialogWrap() {
        VerticalLayout wrap = new VerticalLayout();
        wrap.setPadding(false);
        wrap.setSpacing(false);
        wrap.setMargin(false);
        wrap.setWidth("430px");
        wrap.getStyle()
                .set("padding", "0")
                .set("margin", "0")
                .set("background", "linear-gradient(180deg, rgba(12,19,36,0.99) 0%, rgba(15,23,42,0.99) 100%)")
                .set("border", C_BORDER)
                .set("border-radius", "24px")
                .set("overflow", "visible")
                .set("box-shadow", "0 24px 70px rgba(0,0,0,.34)");
        return wrap;
    }

    private Div buildDialogHeader(String titleText, Dialog dlg) {
        Div header = new Div();
        header.getStyle()
                .set("padding", "18px 20px")
                .set("background", GRAD_PRIMARY)
                .set("position", "relative")
                .set("width", "100%")
                .set("box-sizing", "border-box")
                .set("border-top-left-radius", "24px")
                .set("border-top-right-radius", "24px");

        H4 title = new H4(titleText);
        title.getStyle()
                .set("margin", "0")
                .set("font-size", "17px")
                .set("font-weight", "800")
                .set("color", "#ffffff");

        Button closeBtn = new Button(VaadinIcon.CLOSE_SMALL.create(), e -> dlg.close());
        closeBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeBtn.getStyle()
                .set("position", "absolute")
                .set("top", "10px")
                .set("right", "10px")
                .set("width", "34px")
                .set("height", "34px")
                .set("min-width", "34px")
                .set("padding", "0")
                .set("border-radius", "10px")
                .set("background", "rgba(255,255,255,0.14)")
                .set("color", "white")
                .set("cursor", "pointer");

        header.add(title, closeBtn);
        return header;
    }

    private VerticalLayout buildDialogBody() {
        VerticalLayout body = new VerticalLayout();
        body.setPadding(false);
        body.setSpacing(false);
        body.setMargin(false);
        body.setWidthFull();
        body.getStyle()
                .set("padding", "20px")
                .set("gap", "14px")
                .set("background", "transparent")
                .set("box-sizing", "border-box");
        return body;
    }

    private HorizontalLayout buildDialogActions() {
        HorizontalLayout actions = new HorizontalLayout();
        actions.setWidthFull();
        actions.setPadding(false);
        actions.setSpacing(false);
        actions.setJustifyContentMode(JustifyContentMode.END);
        actions.setAlignItems(FlexComponent.Alignment.CENTER);
        actions.getStyle().set("gap", "10px");
        return actions;
    }

    private Button dialogSecondaryButton(String text, Runnable action) {
        Button btn = new Button(text, e -> action.run());
        btn.getStyle()
                .set("height", "40px")
                .set("padding", "0 18px")
                .set("border-radius", R_MD)
                .set("border", C_BORDER_STRONG)
                .set("background", C_CARD_ALT)
                .set("color", C_TEXT_1)
                .set("font-weight", "700")
                .set("cursor", "pointer");
        return btn;
    }

    private TextField styledField(String label, String placeholder) {
        TextField f = new TextField(label);
        f.setPlaceholder(placeholder);
        f.setWidthFull();
        styleTextField(f);
        return f;
    }

    private DatePicker styledDatePicker() {
        DatePicker dp = new DatePicker("Sana");
        dp.setValue(LocalDate.now());
        dp.setWidthFull();
        styleDatePicker(dp);
        return dp;
    }

    private void styleTextField(TextField field) {
        field.getStyle()
                .set("--vaadin-input-field-background", "rgba(10,18,34,0.98)")
                .set("--vaadin-input-field-value-color", "#e2e8f0")
                .set("--vaadin-input-field-label-color", "#64748b")
                .set("--vaadin-input-field-placeholder-color", "rgba(148,163,184,0.40)")
                .set("--vaadin-input-field-border-color", "rgba(148,163,184,0.12)")
                .set("--vaadin-input-field-focused-border-color", "rgba(59,130,246,0.38)");
    }

    private void styleCombo(ComboBox<?> comboBox) {
        comboBox.getStyle()
                .set("--vaadin-input-field-background", "rgba(10,18,34,0.98)")
                .set("--vaadin-input-field-value-color", "#e2e8f0")
                .set("--vaadin-input-field-label-color", "#64748b")
                .set("--vaadin-input-field-placeholder-color", "rgba(148,163,184,0.40)")
                .set("--vaadin-input-field-border-color", "rgba(148,163,184,0.12)")
                .set("--vaadin-input-field-focused-border-color", "rgba(59,130,246,0.38)");
    }

    private void styleDatePicker(DatePicker dp) {
        dp.getStyle()
                .set("--vaadin-input-field-background", "rgba(10,18,34,0.98)")
                .set("--vaadin-input-field-value-color", "#e2e8f0")
                .set("--vaadin-input-field-label-color", "#64748b")
                .set("--vaadin-input-field-placeholder-color", "rgba(148,163,184,0.40)")
                .set("--vaadin-input-field-border-color", "rgba(148,163,184,0.12)")
                .set("--vaadin-input-field-focused-border-color", "rgba(59,130,246,0.38)");

        dp.addOpenedChangeListener(event -> {
            if (event.isOpened()) {
                dp.getElement().executeJs("""
                    const overlay = this.$.overlay;
                    if (!overlay) return;

                    overlay.style.zIndex = '99999';
                    const sr = overlay.shadowRoot;
                    if (!sr) return;

                    const backdrop = sr.querySelector('[part="backdrop"]');
                    const overlayPart = sr.querySelector('[part="overlay"]');
                    const content = sr.querySelector('[part="content"]');

                    if (backdrop) {
                        backdrop.style.background = 'transparent';
                        backdrop.style.backdropFilter = 'none';
                        backdrop.style.webkitBackdropFilter = 'none';
                    }

                    if (overlayPart) {
                        overlayPart.style.background = '#ffffff';
                        overlayPart.style.border = '1px solid rgba(15,23,42,0.10)';
                        overlayPart.style.borderRadius = '14px';
                        overlayPart.style.boxShadow = '0 18px 40px rgba(0,0,0,0.20)';
                        overlayPart.style.overflow = 'hidden';
                    }

                    if (content) {
                        content.style.background = '#ffffff';
                        content.style.borderRadius = '14px';
                    }
                """);
            }
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean authenticated = auth != null
                && auth.isAuthenticated()
                && !(auth instanceof AnonymousAuthenticationToken)
                && !"anonymousUser".equals(auth.getName());

        if (!authenticated) {
            event.forwardTo("login");
        }
    }
}