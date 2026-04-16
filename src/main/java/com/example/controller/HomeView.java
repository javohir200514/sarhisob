package com.example.controller;

import com.example.companent.MainLayout;
import com.example.dto.ExpenseDTO;
import com.example.dto.PersonalInfoDTO;
import com.example.dto.StatsDTO;
import com.example.service.ExpenseService;
import com.example.service.StatsService;
import com.example.service.UserService;
import com.example.util.SpringSecurityUtil;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import jakarta.annotation.security.RolesAllowed;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Route(value = "", layout = MainLayout.class)
@RolesAllowed("ROLE_USER")
public class HomeView extends VerticalLayout {

    private final StatsService statsService;
    private final ExpenseService expenseService;
    private final UserService userService;

    public HomeView(StatsService statsService, ExpenseService expenseService, UserService userService) {
        this.statsService = statsService;
        this.expenseService = expenseService;
        this.userService = userService;

        setPadding(false);
        setSpacing(false);
        setWidthFull();
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        getStyle()
                .set("background",
                        "radial-gradient(ellipse 80% 60% at 20% -10%, rgba(59,130,246,0.16) 0%, transparent 60%), " +
                                "radial-gradient(ellipse 60% 50% at 80% 10%, rgba(139,92,246,0.12) 0%, transparent 55%), " +
                                "linear-gradient(160deg, #040816 0%, #081225 45%, #050914 100%)")
                .set("background-attachment", "fixed")
                .set("min-height", "100vh")
                .set("overflow", "hidden")
                .set("position", "relative");

        add(
                createHeroSection(),
                createFeatureCarousel(),
                createHowItWorksSection(),
                telegramSection(),
                createFooter()
        );

        injectMoneyEffects();
    }

    private Component createHeroSection() {
        VerticalLayout hero = new VerticalLayout();
        hero.setWidthFull();
        hero.setAlignItems(FlexComponent.Alignment.CENTER);
        hero.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        hero.setPadding(false);
        hero.setSpacing(false);

        hero.getStyle()
                .set("min-height", "100vh")
                .set("padding", "100px 40px 80px")
                .set("position", "relative")
                .set("overflow", "hidden")
                .set("background", "#050b18");

        Div noise = new Div();
        noise.getStyle()
                .set("position", "absolute").set("inset", "0")
                .set("background-image",
                        "url(\"data:image/svg+xml,%3Csvg viewBox='0 0 256 256' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='n'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.9' numOctaves='4' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23n)' opacity='0.04'/%3E%3C/svg%3E\")")
                .set("background-size", "200px 200px")
                .set("pointer-events", "none").set("z-index", "0").set("opacity", "0.6");

        Div orb1 = new Div();
        orb1.getStyle()
                .set("position", "absolute").set("width", "900px").set("height", "900px")
                .set("border-radius", "50%")
                .set("background", "radial-gradient(circle, rgba(37,99,235,0.22) 0%, rgba(37,99,235,0.06) 40%, transparent 70%)")
                .set("top", "-300px").set("left", "-200px")
                .set("pointer-events", "none").set("z-index", "0");

        Div orb2 = new Div();
        orb2.getStyle()
                .set("position", "absolute").set("width", "700px").set("height", "700px")
                .set("border-radius", "50%")
                .set("background", "radial-gradient(circle, rgba(124,58,237,0.20) 0%, rgba(124,58,237,0.05) 45%, transparent 70%)")
                .set("top", "-200px").set("right", "-100px")
                .set("pointer-events", "none").set("z-index", "0");

        Div orb3 = new Div();
        orb3.getStyle()
                .set("position", "absolute").set("width", "500px").set("height", "500px")
                .set("border-radius", "50%")
                .set("background", "radial-gradient(circle, rgba(16,185,129,0.10) 0%, transparent 65%)")
                .set("bottom", "0").set("right", "10%")
                .set("pointer-events", "none").set("z-index", "0");

        Div moneyRainLayer = new Div();
        moneyRainLayer.setId("money-rain");
        moneyRainLayer.getStyle()
                .set("position", "absolute").set("inset", "0")
                .set("pointer-events", "none").set("overflow", "hidden").set("z-index", "0");

        Div gridLines = new Div();
        gridLines.getStyle()
                .set("position", "absolute").set("inset", "0")
                .set("background-image",
                        "linear-gradient(rgba(96,165,250,0.04) 1px, transparent 1px), " +
                                "linear-gradient(90deg, rgba(96,165,250,0.04) 1px, transparent 1px)")
                .set("background-size", "60px 60px")
                .set("pointer-events", "none").set("z-index", "0");

        Div mainGrid = new Div();
        mainGrid.getStyle()
                .set("display", "grid")
                .set("grid-template-columns", "1fr 1.05fr")
                .set("gap", "48px")
                .set("width", "100%").set("max-width", "1280px")
                .set("align-items", "center")
                .set("position", "relative").set("z-index", "2");

        Div left = new Div();
        left.getStyle().set("display", "flex").set("flex-direction", "column").set("gap", "0");

        Div badge = new Div();
        badge.getStyle()
                .set("display", "inline-flex").set("align-items", "center").set("gap", "8px")
                .set("width", "fit-content")
                .set("padding", "8px 18px").set("border-radius", "999px")
                .set("background", "rgba(37,99,235,0.12)")
                .set("border", "1px solid rgba(96,165,250,0.22)")
                .set("margin-bottom", "30px");

        Div pulseDot = new Div();
        pulseDot.getStyle()
                .set("width", "7px").set("height", "7px").set("border-radius", "50%")
                .set("background", "#4ade80").set("box-shadow", "0 0 0 3px rgba(74,222,128,0.25)");
        pulseDot.setId("hero-pulse-dot");

        Span badgeText = new Span("Real-time moliyaviy nazorat");
        badgeText.getStyle()
                .set("font-size", "13px").set("color", "#93c5fd").set("font-weight", "600")
                .set("letter-spacing", "0.02em");

        badge.add(pulseDot, badgeText);

        H1 title = new H1();
        title.getElement().setProperty("innerHTML",
                "Xarajatlaringizni<br>" +
                        "<span style=\"" +
                        "background: linear-gradient(135deg, #60a5fa 0%, #a78bfa 45%, #f472b6 100%);" +
                        "-webkit-background-clip: text; -webkit-text-fill-color: transparent;" +
                        "background-clip: text;" +
                        "\">aqlli boshqaring</span>"
        );
        title.getStyle()
                .set("font-size", "clamp(2.8rem, 5.5vw, 4.6rem)")
                .set("font-weight", "900")
                .set("line-height", "1.05")
                .set("letter-spacing", "-0.045em")
                .set("color", "#f8fafc")
                .set("margin", "0 0 24px 0")
                .set("max-width", "640px");

        Paragraph desc = new Paragraph(
                "MoneyFlow bilan kunlik, oylik va yillik xarajatlaringizni avtomatik tahlil qiling. " +
                        "Telegram bot orqali istalgan joydan boshqaring."
        );
        desc.getStyle()
                .set("font-size", "17px").set("color", "#94a3b8")
                .set("line-height", "1.8").set("max-width", "520px")
                .set("margin", "0 0 38px 0");

        HorizontalLayout btns = new HorizontalLayout();
        btns.setSpacing(false);
        btns.setPadding(false);
        btns.setAlignItems(FlexComponent.Alignment.CENTER);
        btns.getStyle().set("gap", "14px").set("flex-wrap", "wrap");

        Button startBtn = new Button("Hozir boshlash →");
        startBtn.getStyle()
                .set("background", "linear-gradient(135deg, #2563eb 0%, #7c3aed 100%)")
                .set("color", "white").set("border", "none")
                .set("padding", "0 36px").set("height", "58px").set("border-radius", "16px")
                .set("font-weight", "800").set("font-size", "16px").set("cursor", "pointer")
                .set("box-shadow", "0 16px 40px rgba(37,99,235,0.35), 0 0 0 1px rgba(124,58,237,0.3)")
                .set("transition", "all 0.25s ease").set("letter-spacing", "-0.01em");
        startBtn.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(ExpensesView.class)));
        startBtn.getElement().addEventListener("mouseenter", e ->
                startBtn.getStyle().set("transform", "translateY(-3px)").set("box-shadow", "0 24px 50px rgba(37,99,235,0.45), 0 0 0 1px rgba(124,58,237,0.4)"));
        startBtn.getElement().addEventListener("mouseleave", e ->
                startBtn.getStyle().set("transform", "translateY(0)").set("box-shadow", "0 16px 40px rgba(37,99,235,0.35), 0 0 0 1px rgba(124,58,237,0.3)"));

        Button demoBtn = new Button();
        demoBtn.getElement().setProperty("innerHTML",
                "<span style='display:flex;align-items:center;gap:9px'>" +
                        "<span style='width:26px;height:26px;border-radius:50%;background:rgba(255,255,255,0.10);" +
                        "display:flex;align-items:center;justify-content:center;flex-shrink:0'>" +
                        "<svg width='8' height='8' viewBox='0 0 10 10' fill='white' style='margin-left:1px'>" +
                        "<polygon points='2,1 9,5 2,9'/></svg></span>" +
                        "<span style='color:#94a3b8;font-size:15px;font-weight:600'>Demo ko'rish</span></span>");
        demoBtn.getStyle()
                .set("background", "rgba(255,255,255,0.04)")
                .set("border", "1px solid rgba(255,255,255,0.10)")
                .set("padding", "0 24px").set("height", "58px").set("border-radius", "16px")
                .set("cursor", "pointer").set("transition", "all 0.22s");
        demoBtn.getElement().addEventListener("mouseenter", e ->
                demoBtn.getStyle().set("background", "rgba(255,255,255,0.08)").set("border-color", "rgba(255,255,255,0.18)").set("transform", "translateY(-2px)"));
        demoBtn.getElement().addEventListener("mouseleave", e ->
                demoBtn.getStyle().set("background", "rgba(255,255,255,0.04)").set("border-color", "rgba(255,255,255,0.10)").set("transform", "translateY(0)"));
        demoBtn.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(ExpensesView.class)));

        btns.add(startBtn, demoBtn);

        HorizontalLayout proof = new HorizontalLayout();
        proof.setSpacing(false);
        proof.setPadding(false);
        proof.setAlignItems(FlexComponent.Alignment.CENTER);
        proof.getStyle().set("gap", "12px").set("margin-top", "32px");

        HorizontalLayout avatars = new HorizontalLayout();
        avatars.setSpacing(false);
        avatars.setPadding(false);

        List<PersonalInfoDTO> users;
        try {
            users = userService.getLastUsers(5);
        } catch (Exception e) {
            users = new ArrayList<>();
        }

        for (int i = 0; i < users.size(); i++) {
            avatars.add(createUserAvatar(users.get(i), i, users.size()));
        }

        StatsDTO globalStats;
        try {
            globalStats = statsService.getStats();
        } catch (Exception ex) {
            globalStats = new StatsDTO();
        }

        String usersText = String.valueOf(globalStats.getUsers());
        String trackedAmountText = formatMoneyShort(globalStats.getTotalAmount());
        String transactionsText = String.valueOf(expensesCountSafe());

        long userCount = statsService.getUserCount();

        Div proofText = new Div();
        proofText.getElement().setProperty("innerHTML",
                "<div style='font-size:13px;color:#94a3b8'>" +
                        "<strong style='color:#e2e8f0'>" + userCount + "+</strong> foydalanuvchi ishonadi</div>");

        proof.add(avatars, proofText);

        HorizontalLayout chips = new HorizontalLayout();
        chips.setSpacing(false);
        chips.setPadding(false);
        chips.getStyle().set("gap","10px").set("margin-top","24px").set("flex-wrap","wrap");
        System.out.println("--------------------------------------------"+usersText);
        chips.add(
                statChip("👥", usersText + "+", "Foydalanuvchilar"),
                statChip("💰", trackedAmountText, "Kuzatilgan summa"),
                statChip("🧾", transactionsText, "Jami tranzaksiyalar")
        );

        left.add(badge, title, desc, btns, proof, chips);

        Div right = new Div();
        right.getStyle()
                .set("position", "relative")
                .set("display", "flex").set("align-items", "center").set("justify-content", "center");

        Div dashGlow = new Div();
        dashGlow.getStyle()
                .set("position", "absolute").set("inset", "-40px")
                .set("background", "radial-gradient(ellipse 80% 70% at 50% 50%, rgba(37,99,235,0.18) 0%, transparent 65%)")
                .set("filter", "blur(20px)").set("z-index", "0").set("pointer-events", "none");

        Div dash = new Div();
        dash.getStyle()
                .set("position", "relative").set("z-index", "1")
                .set("width", "100%").set("max-width", "580px")
                .set("background", "linear-gradient(145deg, rgba(8,14,30,0.97) 0%, rgba(12,20,40,0.95) 100%)")
                .set("border", "1px solid rgba(96,165,250,0.16)")
                .set("border-radius", "28px")
                .set("padding", "24px")
                .set("box-shadow", "0 32px 80px rgba(0,0,0,0.5), 0 0 0 1px rgba(255,255,255,0.03), inset 0 1px 0 rgba(255,255,255,0.06)");

        HorizontalLayout dashHeader = new HorizontalLayout();
        dashHeader.setWidthFull();
        dashHeader.setPadding(false);
        dashHeader.setSpacing(false);
        dashHeader.setAlignItems(FlexComponent.Alignment.CENTER);
        dashHeader.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        dashHeader.getStyle().set("margin-bottom","20px");

        Div dashLeft = new Div();
        dashLeft.getStyle().set("display","flex").set("flex-direction","column").set("gap","2px");
        Span dashTitle = new Span("MoneyFlow Dashboard");
        dashTitle.getStyle().set("font-size","14px").set("font-weight","800").set("color","#f1f5f9");
        Span dashSub = new Span("Real vaqt rejimi");
        dashSub.getStyle().set("font-size","11px").set("color","#475569");
        dashLeft.add(dashTitle, dashSub);

        HorizontalLayout winDots = new HorizontalLayout();
        winDots.setSpacing(false);
        winDots.setPadding(false);
        winDots.getStyle().set("gap","6px");
        for (String c : new String[]{"#f43f5e","#f59e0b","#4ade80"}) {
            Div dot = new Div();
            dot.getStyle().set("width","11px").set("height","11px").set("border-radius","50%").set("background",c);
            winDots.add(dot);
        }

        dashHeader.add(dashLeft, winDots);

        HorizontalLayout kpiRow = new HorizontalLayout();
        kpiRow.setWidthFull();
        kpiRow.setPadding(false);
        kpiRow.setSpacing(false);
        kpiRow.getStyle().set("gap","10px").set("margin-bottom","18px");

        List<ExpenseDTO> expenses;
        try {
            expenses = expenseService.getAllByCurrentUser();
        } catch (Exception ex) {
            expenses = new ArrayList<>();
        }

        LocalDate today = LocalDate.now();
        BigDecimal daily = sumByDate(expenses, today);
        BigDecimal monthly = sumByMonth(expenses, today.getYear(), today.getMonth());
        BigDecimal yearly = sumByYear(expenses, today.getYear());

        BigDecimal yesterday = sumByDate(expenses, today.minusDays(1));
        BigDecimal lastMonth = sumByMonth(
                expenses,
                today.minusMonths(1).getYear(),
                today.minusMonths(1).getMonth()
        );
        BigDecimal lastYear = sumByYear(expenses, today.getYear() - 1);

        String dailyChange = calculateChangePercent(yesterday, daily);
        String monthlyChange = calculateChangePercent(lastMonth, monthly);
        String yearlyChange = calculateChangePercent(lastYear, yearly);

        kpiRow.add(
                kpiCard("Bugun", formatMoneyPlain(daily), "#38bdf8", dailyChange),
                kpiCard("Oy", formatMoneyPlain(monthly), "#a78bfa", monthlyChange),
                kpiCard("Yil", formatMoneyPlain(yearly), "#fb7185", yearlyChange)
        );

        HorizontalLayout chartHdr = new HorizontalLayout();
        chartHdr.setWidthFull();
        chartHdr.setPadding(false);
        chartHdr.setSpacing(false);
        chartHdr.setAlignItems(FlexComponent.Alignment.CENTER);
        chartHdr.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        chartHdr.getStyle().set("margin-bottom","14px");

        Span chartTitle = new Span("Haftalik xarajat trendi");
        chartTitle.getStyle().set("font-size","12px").set("font-weight","700").set("color","#94a3b8");

        HorizontalLayout legend = new HorizontalLayout();
        legend.setSpacing(false);
        legend.setPadding(false);
        legend.getStyle().set("gap","12px");
        legend.add(legendItem("#38bdf8","Xarajat"), legendItem("#a78bfa","O'rtacha"));

        chartHdr.add(chartTitle, legend);

        List<BigDecimal> weeklyTotals = buildLast7DaysTotals(expenses);
        List<Integer> heights = normalizeBarHeights(weeklyTotals, 8, 110);
        String[] dayLabels = {"Du","Se","Ch","Pa","Ju","Sh","Ya"};

        Div chartWrap = new Div();
        chartWrap.getStyle()
                .set("background","rgba(255,255,255,0.02)")
                .set("border","1px solid rgba(255,255,255,0.05)")
                .set("border-radius","18px").set("padding","18px 16px 12px")
                .set("margin-bottom","16px");

        Div barsAndLabels = new Div();
        barsAndLabels.getStyle()
                .set("display","flex").set("align-items","end")
                .set("justify-content","space-between").set("gap","8px")
                .set("height","120px").set("position","relative");

        for (String top : new String[]{"0%","33%","66%"}) {
            Div guide = new Div();
            guide.getStyle()
                    .set("position","absolute").set("left","0").set("right","0")
                    .set("top",top).set("height","1px")
                    .set("background","rgba(255,255,255,0.04)").set("pointer-events","none");
            barsAndLabels.add(guide);
        }

        for (int i = 0; i < heights.size(); i++) {
            Div col = new Div();
            col.getStyle().set("flex","1").set("display","flex").set("flex-direction","column")
                    .set("align-items","center").set("justify-content","flex-end").set("gap","6px").set("position","relative");

            Div bar = new Div();
            bar.getStyle()
                    .set("width","100%").set("height", heights.get(i)+"px")
                    .set("border-radius","7px 7px 3px 3px")
                    .set("background","linear-gradient(180deg, #38bdf8 0%, #2563eb 55%, #7c3aed 100%)")
                    .set("box-shadow","0 0 16px rgba(56,189,248,0.18)");

            Span val = new Span(formatMoneyShort(weeklyTotals.get(i)));
            val.getStyle()
                    .set("position","absolute").set("bottom", (heights.get(i)+6)+"px")
                    .set("font-size","9px").set("font-weight","700").set("color","#94a3b8")
                    .set("white-space","nowrap");

            col.add(val, bar);
            barsAndLabels.add(col);
        }
        chartWrap.add(barsAndLabels);

        HorizontalLayout dayRow = new HorizontalLayout();
        dayRow.setWidthFull();
        dayRow.setPadding(false);
        dayRow.setSpacing(false);
        dayRow.getStyle().set("gap","8px").set("margin-top","8px");
        for (int i = 0; i < 7; i++) {
            Span d = new Span(dayLabels[i]);
            d.getStyle()
                    .set("flex","1").set("text-align","center")
                    .set("font-size","10px").set("color","#334155").set("font-weight","600");
            dayRow.add(d);
        }
        chartWrap.add(dayRow);

        VerticalLayout txSection = new VerticalLayout();
        txSection.setPadding(false);
        txSection.setSpacing(false);
        txSection.getStyle().set("gap","8px");

        HorizontalLayout txHeader = new HorizontalLayout();
        txHeader.setWidthFull();
        txHeader.setPadding(false);
        txHeader.setSpacing(false);
        txHeader.setAlignItems(FlexComponent.Alignment.CENTER);
        txHeader.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        txHeader.getStyle().set("margin-bottom","4px");

        Span txTitle = new Span("So'nggi tranzaksiyalar");
        txTitle.getStyle().set("font-size","12px").set("font-weight","700").set("color","#94a3b8");

        Span txSeeAll = new Span("Barchasi →");
        txSeeAll.getStyle().set("font-size","11px").set("color","#3b82f6").set("cursor","pointer").set("font-weight","600");

        txHeader.add(txTitle, txSeeAll);
        txSection.add(txHeader);

        List<ExpenseDTO> recent = expenses.stream()
                .filter(e -> e.getDate() != null)
                .sorted(Comparator.comparing(ExpenseDTO::getDate).reversed())
                .limit(3)
                .collect(Collectors.toList());

        if (recent.isEmpty()) {
            txSection.add(emptyTxRow());
        } else {
            for (ExpenseDTO ex : recent) {
                String cat = ex.getCategory() == null ? "Other" : ex.getCategory();
                txSection.add(txRow(cat, "-" + formatMoneyPlain(ex.getAmount()), ex.getDate(), getColorForCategory(cat)));
            }
        }

        dash.add(dashHeader, kpiRow, chartHdr, chartWrap, txSection);
        right.add(dashGlow, dash);

        mainGrid.add(left, right);

        mainGrid.getElement().executeJs("""
        const mq = window.matchMedia('(max-width: 960px)');
        const host = this;
        function apply() {
            host.style.gridTemplateColumns = mq.matches ? '1fr' : '1fr 1.05fr';
        }
        apply();
        mq.addEventListener('change', apply);
    """);

        hero.getElement().executeJs("""
        const dot = document.getElementById('hero-pulse-dot');
        if (dot) {
            dot.animate(
                [{boxShadow:'0 0 0 0 rgba(74,222,128,0.5)'},
                 {boxShadow:'0 0 0 8px rgba(74,222,128,0)'}],
                {duration:1800, iterations:Infinity}
            );
        }
    """);

        hero.add(moneyRainLayer, noise, orb1, orb2, orb3, gridLines, mainGrid);
        return hero;
    }

    private Component createUserAvatar(PersonalInfoDTO user, int index, int totalUsers) {
        String fullName = ((user.getName() == null ? "" : user.getName()) + " " +
                (user.getSurname() == null ? "" : user.getSurname())).trim();

        String initials = getInitials(fullName);

        Div wrapper = new Div();
        wrapper.getStyle()
                .set("width", "30px")
                .set("height", "30px")
                .set("position", "relative")
                .set("margin-left", index == 0 ? "0" : "-8px")
                .set("z-index", String.valueOf(totalUsers - index))
                .set("flex-shrink", "0");

        Div fallback = new Div();
        fallback.setText(initials);
        fallback.getStyle()
                .set("width", "30px")
                .set("height", "30px")
                .set("border-radius", "50%")
                .set("border", "2px solid #050b18")
                .set("background", getAvatarColor(index))
                .set("color", "white")
                .set("font-size", "10px")
                .set("font-weight", "700")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("position", "absolute")
                .set("top", "0")
                .set("left", "0");

        if (user.getPhotoId() != null && !user.getPhotoId().isBlank()) {
            Image img = new Image("/profiles/" + user.getPhotoId(), fullName);
            img.getStyle()
                    .set("width", "30px")
                    .set("height", "30px")
                    .set("border-radius", "50%")
                    .set("object-fit", "cover")
                    .set("border", "2px solid #050b18")
                    .set("position", "absolute")
                    .set("top", "0")
                    .set("left", "0")
                    .set("background", "#0f172a");

            img.getElement().addEventListener("error", e -> img.setVisible(false));

            wrapper.add(fallback, img);
        } else {
            wrapper.add(fallback);
        }

        return wrapper;
    }
    private Component createFeatureCarousel() {

        Div wrapper = new Div();
        wrapper.setWidthFull();
        wrapper.getStyle()
                .set("padding", "20px 0")
                .set("position", "relative");

        Div viewport = new Div();
        viewport.setWidthFull();
        viewport.getStyle()
                .set("overflow", "hidden")
                .set("padding", "0 48px")
                .set("box-sizing", "border-box");

        Div track = new Div();
        track.getStyle()
                .set("display", "flex")
                .set("gap", "16px")
                .set("width", "max-content")
                .set("will-change", "transform")
                .set("animation", "mf-scrollX 28s linear infinite");

        String iconChart =
                "<svg width='28' height='28' viewBox='0 0 30 30' fill='none' xmlns='http://www.w3.org/2000/svg'>" +
                        "<path d='M5 23L10 16.5L14.5 19.5L21 10.5L25 14.5' stroke='currentColor' stroke-width='1.7' stroke-linecap='round' stroke-linejoin='round'/>" +
                        "<circle cx='10' cy='16.5' r='1.7' fill='currentColor'/>" +
                        "<circle cx='14.5' cy='19.5' r='1.7' fill='currentColor'/>" +
                        "<circle cx='21' cy='10.5' r='1.7' fill='currentColor'/>" +
                        "<circle cx='25' cy='14.5' r='1.7' fill='currentColor'/>" +
                        "</svg>";

        String iconTelegram =
                "<svg width='28' height='28' viewBox='0 0 30 30' fill='none' xmlns='http://www.w3.org/2000/svg'>" +
                        "<circle cx='15' cy='15' r='11' stroke='currentColor' stroke-width='1.7'/>" +
                        "<path d='M21.8 10.2L18.9 23L14.4 19.1L12 21.2L12.6 16.7L20.4 11.6L10.7 15.3L8.4 14.5L21.8 10.2Z' fill='currentColor' opacity='0.92'/>" +
                        "</svg>";

        String iconShield =
                "<svg width='28' height='28' viewBox='0 0 30 30' fill='none' xmlns='http://www.w3.org/2000/svg'>" +
                        "<path d='M15 4L23 7.8V14.5C23 19.8 19.5 23.5 15 25.3C10.5 23.5 7 19.8 7 14.5V7.8L15 4Z' stroke='currentColor' stroke-width='1.7' stroke-linejoin='round'/>" +
                        "<path d='M11.5 15.2L14.1 17.8L18.8 12.9' stroke='currentColor' stroke-width='1.7' stroke-linecap='round' stroke-linejoin='round'/>" +
                        "</svg>";

        String iconInsight =
                "<svg width='28' height='28' viewBox='0 0 30 30' fill='none' xmlns='http://www.w3.org/2000/svg'>" +
                        "<path d='M15 5.5C10.3 5.5 6.5 9.3 6.5 14C6.5 16.3 7.4 18.1 9 19.6C10 20.6 10.7 21.6 10.9 23H19.1C19.3 21.6 20 20.6 21 19.6C22.6 18.1 23.5 16.3 23.5 14C23.5 9.3 19.7 5.5 15 5.5Z' stroke='currentColor' stroke-width='1.7' stroke-linejoin='round'/>" +
                        "<path d='M12.2 26H17.8' stroke='currentColor' stroke-width='1.7' stroke-linecap='round'/>" +
                        "</svg>";

        String iconDashboard =
                "<svg width='28' height='28' viewBox='0 0 30 30' fill='none' xmlns='http://www.w3.org/2000/svg'>" +
                        "<rect x='5' y='7' width='20' height='13' rx='3.5' stroke='currentColor' stroke-width='1.7'/>" +
                        "<path d='M5 12h20' stroke='currentColor' stroke-width='1.7'/>" +
                        "</svg>";

        Div c1 = buildFeatureCardSVG("01", iconDashboard, "Tez boshqaruv", "1 soniyada qo'shish");
        Div c2 = buildFeatureCardSVG("02", iconTelegram, "Telegram", "Bot orqali boshqarish");
        Div c3 = buildFeatureCardSVG("03", iconChart, "Statistika", "Real-time analiz");
        Div c4 = buildFeatureCardSVG("04", iconShield, "Xavfsiz", "Ma'lumot himoyasi");
        Div c5 = buildFeatureCardSVG("05", iconInsight, "Insight", "Aqlli tavsiyalar");

        track.add(c1, c2, c3, c4, c5);

        // loop uchun duplicate
        track.add(
                buildFeatureCardSVG("01", iconDashboard, "Tez boshqaruv", "1 soniyada qo'shish"),
                buildFeatureCardSVG("02", iconTelegram, "Telegram", "Bot orqali boshqarish"),
                buildFeatureCardSVG("03", iconChart, "Statistika", "Real-time analiz"),
                buildFeatureCardSVG("04", iconShield, "Xavfsiz", "Ma'lumot himoyasi"),
                buildFeatureCardSVG("05", iconInsight, "Insight", "Aqlli tavsiyalar")
        );

        viewport.add(track);
        wrapper.add(viewport);

        wrapper.getElement().executeJs("""
        if (!document.getElementById('mf-carousel-style')) {
            const style = document.createElement('style');
            style.id = 'mf-carousel-style';
            style.textContent = `
                @keyframes mf-scrollX {
                    0%   { transform: translateX(0); }
                    100% { transform: translateX(calc(-50% - 8px)); }
                }
            `;
            document.head.appendChild(style);
        }
    """);

        track.getElement().executeJs("""
        this.addEventListener('mouseenter', () => {
            this.style.animationPlayState = 'paused';
        });
        this.addEventListener('mouseleave', () => {
            this.style.animationPlayState = 'running';
        });
    """);

        viewport.getElement().executeJs("""
        const mq = window.matchMedia('(max-width: 768px)');
        const host = this;
        function apply() {
            host.style.padding = mq.matches ? '0 20px' : '0 48px';
        }
        apply();
        mq.addEventListener('change', apply);
    """);

        return wrapper;
    }

    private Div buildFeatureCardSVG(String num, String svg, String title, String desc) {

        Div card = new Div();
        card.getStyle()
                .set("min-width", "245px")
                .set("max-width", "245px")
                .set("padding", "20px")
                .set("border-radius", "22px")
                .set("background", "rgba(10,18,38,0.95)")
                .set("border", "1px solid rgba(96,165,250,0.10)")
                .set("color", "white")
                .set("box-shadow", "0 10px 30px rgba(0,0,0,0.35)")
                .set("box-sizing", "border-box")
                .set("flex-shrink", "0");

        Span number = new Span(num);
        number.getStyle()
                .set("font-size", "12px")
                .set("font-weight", "700")
                .set("color", "#64748b")
                .set("display", "inline-block")
                .set("margin-bottom", "16px");

        Div icon = new Div();
        icon.getElement().setProperty("innerHTML", svg);
        icon.getStyle()
                .set("width", "48px")
                .set("height", "48px")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("border-radius", "16px")
                .set("background", "rgba(37,99,235,0.12)")
                .set("border", "1px solid rgba(96,165,250,0.12)")
                .set("color", "#f8fafc")
                .set("margin-bottom", "16px");

        H4 t = new H4(title);
        t.getStyle()
                .set("margin", "0 0 8px 0")
                .set("font-size", "16px")
                .set("font-weight", "800")
                .set("color", "#f8fafc")
                .set("line-height", "1.3");

        Paragraph p = new Paragraph(desc);
        p.getStyle()
                .set("margin", "0")
                .set("font-size", "13px")
                .set("line-height", "1.7")
                .set("color", "#94a3b8");

        card.add(number, icon, t, p);
        return card;
    }


    private String getInitials(String name) {
        if (name == null || name.isBlank()) return "?";

        String[] parts = name.trim().split("\\s+");

        if (parts.length == 1) {
            return parts[0].substring(0, 1).toUpperCase();
        }

        return (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase();
    }

    private String getAvatarColor(int index) {
        String[] colors = {
                "#3b82f6",
                "#8b5cf6",
                "#06b6d4",
                "#10b981",
                "#f43f5e",
                "#f59e0b"
        };
        return colors[index % colors.length];
    }
    private String calculateChangePercent(BigDecimal oldValue, BigDecimal newValue) {
        BigDecimal oldVal = oldValue == null ? BigDecimal.ZERO : oldValue;
        BigDecimal newVal = newValue == null ? BigDecimal.ZERO : newValue;

        if (oldVal.compareTo(BigDecimal.ZERO) == 0) {
            if (newVal.compareTo(BigDecimal.ZERO) == 0) {
                return "0%";
            }
            return "+100%";
        }

        BigDecimal percent = newVal.subtract(oldVal)
                .multiply(BigDecimal.valueOf(100))
                .divide(oldVal, 2, java.math.RoundingMode.HALF_UP);

        String sign = percent.compareTo(BigDecimal.ZERO) > 0 ? "+" : "";
        return sign + percent.stripTrailingZeros().toPlainString() + "%";
    }

    private long expensesCountSafe() {
        try {
            List<ExpenseDTO> all = expenseService.getAllByCurrentUser();
            return all == null ? 0 : all.size();
        } catch (Exception e) {
            return 0;
        }
    }

    private Div kpiCard(String label, String value, String color, String change) {
        Div card = new Div();
        card.getStyle()
                .set("flex","1").set("padding","14px 12px").set("border-radius","16px")
                .set("background","rgba(255,255,255,0.03)")
                .set("border","1px solid rgba(255,255,255,0.06)")
                .set("display","flex").set("flex-direction","column").set("gap","6px");

        Div topRow = new Div();
        topRow.getStyle().set("display","flex").set("align-items","center").set("justify-content","space-between");

        Span lbl = new Span(label);
        lbl.getStyle().set("font-size","10px").set("color","#475569").set("font-weight","600").set("text-transform","uppercase").set("letter-spacing","0.06em");

        boolean positive = change.startsWith("+");
        Span chg = new Span(change);
        chg.getStyle()
                .set("font-size","10px").set("font-weight","700")
                .set("color", positive ? "#4ade80" : "#f87171")
                .set("background", positive ? "rgba(74,222,128,0.10)" : "rgba(248,113,113,0.10)")
                .set("padding","2px 6px").set("border-radius","6px");

        topRow.add(lbl, chg);

        Span val = new Span(value);
        val.getStyle()
                .set("font-size","15px").set("font-weight","800").set("color","#f1f5f9")
                .set("letter-spacing","-0.02em");

        Div accentLine = new Div();
        accentLine.getStyle()
                .set("height","2px").set("width","32px").set("border-radius","99px")
                .set("background","linear-gradient(90deg,"+color+",transparent)");

        card.add(topRow, val, accentLine);
        return card;
    }

    private HorizontalLayout legendItem(String color, String label) {
        Div dot = new Div();
        dot.getStyle().set("width","7px").set("height","7px").set("border-radius","50%").set("background",color).set("flex-shrink","0");
        Span txt = new Span(label);
        txt.getStyle().set("font-size","10px").set("color","#475569").set("font-weight","500");

        HorizontalLayout row = new HorizontalLayout(dot, txt);
        row.setSpacing(false);
        row.setPadding(false);
        row.setAlignItems(FlexComponent.Alignment.CENTER);
        row.getStyle().set("gap","5px");
        return row;
    }

    private HorizontalLayout txRow(String name, String amount, LocalDate date, String color) {
        Div colorDot = new Div();
        colorDot.getStyle()
                .set("width","34px").set("height","34px").set("border-radius","10px").set("flex-shrink","0")
                .set("background","rgba(255,255,255,0.04)").set("border","1px solid rgba(255,255,255,0.07)")
                .set("display","flex").set("align-items","center").set("justify-content","center")
                .set("font-size","14px");
        colorDot.setText(getCategoryEmoji(name));

        Div textPart = new Div();
        textPart.getStyle().set("flex","1").set("display","flex").set("flex-direction","column").set("gap","2px");

        Span n = new Span(name);
        n.getStyle().set("font-size","13px").set("font-weight","600").set("color","#e2e8f0");

        Span d = new Span(date != null ? date.toString() : "");
        d.getStyle().set("font-size","10px").set("color","#334155");
        textPart.add(n, d);

        Span amt = new Span(amount);
        amt.getStyle().set("font-size","13px").set("font-weight","800").set("color","#f87171");

        HorizontalLayout row = new HorizontalLayout(colorDot, textPart, amt);
        row.setWidthFull();
        row.setAlignItems(FlexComponent.Alignment.CENTER);
        row.setSpacing(false);
        row.setPadding(false);
        row.getStyle()
                .set("gap","10px").set("padding","10px 12px").set("border-radius","13px")
                .set("background","rgba(255,255,255,0.02)").set("border","1px solid rgba(255,255,255,0.04)")
                .set("transition","background 0.2s");

        row.getElement().addEventListener("mouseenter", e ->
                row.getStyle().set("background","rgba(255,255,255,0.05)"));
        row.getElement().addEventListener("mouseleave", e ->
                row.getStyle().set("background","rgba(255,255,255,0.02)"));
        return row;
    }

    private HorizontalLayout emptyTxRow() {
        Span s = new Span("Hali xarajat qo'shilmagan");
        s.getStyle().set("color","#334155").set("font-size","13px");

        HorizontalLayout row = new HorizontalLayout(s);
        row.setAlignItems(FlexComponent.Alignment.CENTER);
        row.getStyle().set("padding","12px").set("border-radius","13px").set("background","rgba(255,255,255,0.02)");
        return row;
    }

    private Div statChip(String emoji, String value, String label) {
        Div chip = new Div();
        chip.getStyle()
                .set("display","inline-flex").set("align-items","center").set("gap","8px")
                .set("padding","10px 16px").set("border-radius","14px")
                .set("background","rgba(255,255,255,0.04)")
                .set("border","1px solid rgba(255,255,255,0.08)");

        Span em = new Span(emoji);
        em.getStyle().set("font-size","16px");

        Div texts = new Div();
        texts.getStyle().set("display","flex").set("flex-direction","column").set("gap","1px");

        Span v = new Span(value);
        v.getStyle().set("font-size","14px").set("font-weight","800").set("color","#f1f5f9").set("line-height","1");

        Span l = new Span(label);
        l.getStyle().set("font-size","10px").set("color","#475569").set("font-weight","500");

        texts.add(v, l);
        chip.add(em, texts);
        return chip;
    }

    private Div floatingWidget(String emoji, String title, String value, String color) {
        Div box = new Div();
        box.getStyle()
                .set("display","inline-flex").set("align-items","center").set("gap","10px")
                .set("padding","12px 16px").set("border-radius","16px")
                .set("background","rgba(8,14,30,0.92)").set("backdrop-filter","blur(16px)")
                .set("border","1px solid rgba(96,165,250,0.16)")
                .set("box-shadow","0 12px 30px rgba(0,0,0,0.4)");

        Div iconBox = new Div();
        iconBox.setText(emoji);
        iconBox.getStyle()
                .set("width","32px").set("height","32px").set("border-radius","9px")
                .set("background","rgba(255,255,255,0.05)")
                .set("display","flex").set("align-items","center").set("justify-content","center")
                .set("font-size","15px").set("flex-shrink","0");

        Div texts = new Div();
        texts.getStyle().set("display","flex").set("flex-direction","column").set("gap","2px");

        Span t = new Span(title);
        t.getStyle().set("font-size","10px").set("color","#475569").set("font-weight","600").set("white-space","nowrap");

        Span v = new Span(value);
        v.getStyle().set("font-size","13px").set("font-weight","800").set("color", color).set("white-space","nowrap");

        texts.add(t, v);
        box.add(iconBox, texts);
        return box;
    }


    private Component createHowItWorksSection() {
        VerticalLayout section = new VerticalLayout();
        section.setAlignItems(Alignment.CENTER);
        section.setWidthFull();
        section.setPadding(false);
        section.setSpacing(false);

        section.getStyle()
                .set("padding", "80px 28px 0 28px")
                .set("box-sizing", "border-box");

        H2 title = new H2("Qanday ishlaydi");
        title.getStyle()
                .set("font-size", "clamp(1.9rem, 3.5vw, 2.5rem)")
                .set("font-weight", "800")
                .set("margin", "0 0 10px 0")
                .set("color", "#f1f5f9");

        Paragraph subtitle = new Paragraph("Ishni boshlash uchun atigi uchta oddiy qadam");
        subtitle.getStyle()
                .set("color", "#94a3b8")
                .set("font-size", "16px")
                .set("margin", "0 0 44px 0");

        Div grid = new Div();
        grid.getStyle()
                .set("display", "grid")
                .set("grid-template-columns", "repeat(3, 1fr)")
                .set("gap", "20px")
                .set("width", "100%")
                .set("max-width", "1180px")
                .set("align-items", "stretch");

        grid.add(
                stepCard("1", "Ro'yxatdan o'ting yoki tizimga kiring",
                        "Bir necha soniyada bepul akkaunt yarating yoki mavjud akkauntingizga kiring."),
                stepCard("2", "Xarajatlaringizni qo'shing",
                        "Xarajatlaringizni web ilova orqali yoki Telegram bot orqali qo'shing."),
                stepCard("3", "Kuzating va optimallashtiring",
                        "Batafsil statistikani ko'ring va moliyaviy qarorlarni ongli ravishda qabul qiling.")
        );

        section.add(title, subtitle, grid);
        return section;
    }

    private Component stepCard(String number, String titleText, String descText) {
        VerticalLayout card = new VerticalLayout();
        card.setPadding(false);
        card.setSpacing(false);
        card.setWidthFull();

        card.getStyle()
                .set("padding", "32px")
                .set("background", "rgba(15,23,42,0.66)")
                .set("backdrop-filter", "blur(16px)")
                .set("-webkit-backdrop-filter", "blur(16px)")
                .set("border", "1px solid rgba(96,165,250,0.12)")
                .set("border-radius", "22px")
                .set("box-shadow", "0 10px 34px rgba(0,0,0,0.26)")
                .set("height", "100%");

        Div circle = new Div();
        circle.setText(number);
        circle.getStyle()
                .set("width", "52px")
                .set("height", "52px")
                .set("border-radius", "50%")
                .set("background", "linear-gradient(135deg, #2563eb 0%, #7c3aed 100%)")
                .set("box-shadow", "0 0 24px rgba(37,99,235,0.28)")
                .set("color", "white")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("font-size", "20px")
                .set("font-weight", "800")
                .set("margin-bottom", "20px");

        H3 stepTitle = new H3(titleText);
        stepTitle.getStyle()
                .set("margin", "0 0 12px 0")
                .set("color", "#f1f5f9")
                .set("font-size", "18px")
                .set("font-weight", "700");

        Paragraph desc = new Paragraph(descText);
        desc.getStyle()
                .set("margin", "0")
                .set("color", "#94a3b8")
                .set("line-height", "1.7")
                .set("font-size", "14px");

        card.add(circle, stepTitle, desc);
        return card;
    }

    private Component telegramSection() {
        VerticalLayout wrapper = new VerticalLayout();
        wrapper.setWidthFull();
        wrapper.setAlignItems(Alignment.CENTER);
        wrapper.setPadding(false);
        wrapper.setSpacing(false);

        wrapper.getStyle()
                .set("padding", "100px 20px")
                .set("background",
                        "radial-gradient(ellipse 70% 80% at 30% 50%, rgba(37,99,235,0.18) 0%, transparent 60%), " +
                                "radial-gradient(ellipse 60% 70% at 75% 30%, rgba(124,58,237,0.14) 0%, transparent 55%), " +
                                "linear-gradient(160deg, #08101f 0%, #0a1028 50%, #070b18 100%)")
                .set("position", "relative")
                .set("overflow", "hidden")
                .set("margin-top", "80px")
                .set("border-top", "1px solid rgba(96,165,250,0.10)")
                .set("border-bottom", "1px solid rgba(96,165,250,0.08)");

        HorizontalLayout mainContent = new HorizontalLayout();
        mainContent.setWidthFull();
        mainContent.setMaxWidth("1200px");
        mainContent.setAlignItems(Alignment.CENTER);
        mainContent.setJustifyContentMode(JustifyContentMode.BETWEEN);
        mainContent.getStyle().set("flex-wrap", "wrap");

        VerticalLayout leftSide = new VerticalLayout();
        leftSide.setPadding(false);
        leftSide.setSpacing(false);
        leftSide.setWidth("50%");
        leftSide.setAlignItems(Alignment.START);

        Image tgLogo = new Image("images/telegram.png", "TG");
        tgLogo.setWidth("50px");
        tgLogo.getStyle().set("margin-bottom", "20px");

        H2 title = new H2("Telegram Botimizni sinab ko'ring");
        title.getStyle()
                .set("font-size", "clamp(26px, 4vw, 40px)")
                .set("font-weight", "900")
                .set("color", "#f8fafc")
                .set("margin", "0 0 18px 0")
                .set("line-height", "1.15");

        Paragraph desc = new Paragraph(
                "Xarajatlaringizni yo'l-yo'lakay, Telegram orqali soniyalar ichida qo'shing. Har kuni aqlli eslatmalar va haftalik hisobotlarni to'g'ridan-to'g'ri messenjeringizda oling."
        );
        desc.getStyle()
                .set("color", "#94a3b8")
                .set("font-size", "16px")
                .set("max-width", "480px")
                .set("line-height", "1.7")
                .set("margin-bottom", "36px");

        Image botIcon = new Image("images/bot.png", "Bot Icon");
        botIcon.setWidth("36px");
        botIcon.getStyle().set("margin-right", "12px");

        HorizontalLayout btnContent = new HorizontalLayout(botIcon, new Span("@money_flow_bot"));
        btnContent.setAlignItems(Alignment.CENTER);
        btnContent.setSpacing(false);

        Button btn = new Button(btnContent);
        btn.getStyle()
                .set("background", "linear-gradient(135deg, #2563eb 0%, #7c3aed 100%)")
                .set("color", "white")
                .set("padding", "0 32px")
                .set("height", "62px")
                .set("border-radius", "18px")
                .set("font-weight", "800")
                .set("font-size", "16px")
                .set("cursor", "pointer")
                .set("border", "none")
                .set("box-shadow", "0 18px 42px rgba(37,99,235,0.30)")
                .set("transition", "all 0.28s ease");

        btn.addClickListener(e -> getUI().ifPresent(ui -> ui.getPage().open("https://t.me/money_flow_bot")));

        Div rightSide = new Div();
        rightSide.setWidth("45%");
        rightSide.getStyle()
                .set("display", "flex")
                .set("justify-content", "center")
                .set("align-items", "center");

        Image robotFlow = new Image("images/bot_flow.png", "Robot Flow");
        robotFlow.getStyle()
                .set("width", "100%")
                .set("max-width", "420px")
                .set("filter", "drop-shadow(0 20px 60px rgba(37,99,235,0.25))");

        robotFlow.getElement().executeJs(
                "this.animate([" +
                        "{ transform: 'translateY(0px)' }," +
                        "{ transform: 'translateY(-20px)' }," +
                        "{ transform: 'translateY(0px)' }" +
                        "], { duration: 4000, iterations: Infinity });"
        );

        rightSide.add(robotFlow);
        leftSide.add(tgLogo, title, desc, btn);
        mainContent.add(leftSide, rightSide);
        wrapper.add(mainContent);

        return wrapper;
    }

    private Component createFooter() {
        Footer footer = new Footer();
        footer.setWidthFull();
        footer.getStyle()
                .set("background", "linear-gradient(180deg, #040812 0%, #03060f 100%)")
                .set("color", "#64748b")
                .set("padding", "72px 0 28px 0")
                .set("border-top", "1px solid rgba(96,165,250,0.08)")
                .set("box-sizing", "border-box");

        Div container = new Div();
        container.getStyle()
                .set("max-width", "1200px")
                .set("margin", "0 auto")
                .set("padding", "0 20px");

        Div grid = new Div();
        grid.getStyle()
                .set("display", "grid")
                .set("grid-template-columns", "repeat(auto-fit, minmax(280px, 1fr))")
                .set("gap", "56px")
                .set("margin-bottom", "44px");

        VerticalLayout brandCol = new VerticalLayout();
        brandCol.setPadding(false);
        brandCol.setSpacing(false);
        brandCol.getStyle().set("gap", "18px");

        HorizontalLayout logoRow = new HorizontalLayout();
        logoRow.setPadding(false);
        logoRow.setSpacing(false);
        logoRow.setAlignItems(Alignment.CENTER);

        H3 brandName = new H3();
        brandName.getElement().setProperty("innerHTML",
                "<span style='background:linear-gradient(135deg,#60a5fa,#a78bfa,#22d3ee);" +
                        "-webkit-background-clip:text;" +
                        "-webkit-text-fill-color:transparent;" +
                        "background-clip:text;'>" +
                        "Sarhisob</span>"
        );
        brandName.getStyle()
                .set("margin", "0")
                .set("font-size", "26px")
                .set("font-weight", "900")
                .set("letter-spacing", "0.4px")
                .set("font-family", "'Inter', 'Segoe UI', sans-serif")
                .set("line-height", "1.1");

        logoRow.add(brandName);

        Paragraph about = new Paragraph(
                "Shaxsiy moliyaviy holatni kuzatish, xarajat va daromadlarni tartibga solish hamda kundalik hisob-kitobni qulay boshqarish uchun yaratilgan platforma."
        );
        about.getStyle()
                .set("color", "#94a3b8")
                .set("font-size", "14px")
                .set("line-height", "1.75")
                .set("max-width", "390px")
                .set("margin", "0");

        brandCol.add(logoRow, about);

        VerticalLayout navCol = new VerticalLayout();
        navCol.setPadding(false);
        navCol.setSpacing(false);
        navCol.getStyle().set("gap", "12px");

        H4 navTitle = new H4("Sahifalar");
        navTitle.getStyle()
                .set("color", "#cbd5e1")
                .set("margin", "0 0 12px 0")
                .set("font-size", "13px")
                .set("text-transform", "uppercase")
                .set("letter-spacing", "0.1em")
                .set("font-weight", "700");

        navCol.add(
                navTitle,
                createFooterNavLink("Bosh sahifa", HomeView.class),
                createFooterNavLink("Xarajatlar", ExpensesView.class),
                createFooterNavLink("Statistika", StatisticsView.class),
                createFooterNavLink("Bog‘lanish", ContactUsView.class)
        );

        VerticalLayout contactCol = new VerticalLayout();
        contactCol.setPadding(false);
        contactCol.setSpacing(false);
        contactCol.getStyle().set("gap", "12px");

        H4 contactTitle = new H4("Aloqa");
        contactTitle.getStyle()
                .set("color", "#cbd5e1")
                .set("margin", "0 0 12px 0")
                .set("font-size", "13px")
                .set("text-transform", "uppercase")
                .set("letter-spacing", "0.1em")
                .set("font-weight", "700");

        contactCol.add(
                contactTitle,
                createNiceContactRow(VaadinIcon.USER.create(), "Javohir Komilbayev"),
                createNiceContactRow(VaadinIcon.ENVELOPE.create(), "komilbayevj@gmail.com"),
                createNiceContactRow(VaadinIcon.PHONE.create(), "+998 97 791 14 01")
        );

        HorizontalLayout socialIcons = new HorizontalLayout(
                createSocialIconLink("images/telegram.png", "https://t.me/money_flow_bot"),
                createSocialIconLink("images/instagram.png", "#")
        );
        socialIcons.setPadding(false);
        socialIcons.setSpacing(true);
        socialIcons.getStyle()
                .set("margin-top", "14px")
                .set("gap", "10px");

        contactCol.add(socialIcons);

        grid.add(brandCol, navCol, contactCol);

        Hr hr = new Hr();
        hr.getStyle()
                .set("border", "none")
                .set("border-top", "1px solid rgba(96,165,250,0.08)")
                .set("margin", "0 0 24px 0");

        HorizontalLayout bottomRow = new HorizontalLayout();
        bottomRow.setWidthFull();
        bottomRow.setPadding(false);
        bottomRow.setSpacing(false);
        bottomRow.setJustifyContentMode(JustifyContentMode.CENTER);
        bottomRow.setAlignItems(Alignment.CENTER);

        Span copyright = new Span("© 2026 Sarhisob. Barcha huquqlar himoyalangan.");
        copyright.getStyle()
                .set("color", "#64748b")
                .set("font-size", "13px")
                .set("text-align", "center");

        bottomRow.add(copyright);

        container.add(grid, hr, bottomRow);
        footer.add(container);
        return footer;
    }

    private RouterLink createFooterNavLink(String text, Class<? extends Component> navigationTarget) {
        RouterLink link = new RouterLink(text, navigationTarget);
        link.getStyle()
                .set("color", "#94a3b8")
                .set("text-decoration", "none")
                .set("font-size", "14px")
                .set("font-weight", "500")
                .set("transition", "color 0.25s ease");

        link.getElement().addEventListener("mouseenter", e -> link.getStyle().set("color", "#f8fafc"));
        link.getElement().addEventListener("mouseleave", e -> link.getStyle().set("color", "#94a3b8"));
        return link;
    }

    private HorizontalLayout createNiceContactRow(Icon icon, String text) {
        icon.setSize("15px");
        icon.setColor("#60a5fa");

        Span span = new Span(text);
        span.getStyle()
                .set("color", "#94a3b8")
                .set("font-size", "14px")
                .set("font-weight", "500");

        HorizontalLayout row = new HorizontalLayout(icon, span);
        row.setAlignItems(Alignment.CENTER);
        row.getStyle().set("gap", "10px");
        return row;
    }

    private Anchor createSocialIconLink(String imgPath, String url) {
        Image img = new Image(imgPath, "");
        img.setWidth("18px");
        img.setHeight("18px");

        Anchor anchor = new Anchor(url, img);
        anchor.getStyle()
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("width", "36px")
                .set("height", "36px")
                .set("background", "rgba(37,99,235,0.10)")
                .set("border", "1px solid rgba(96,165,250,0.14)")
                .set("border-radius", "10px")
                .set("transition", "all 0.28s ease");

        anchor.getElement().addEventListener("mouseenter", e ->
                anchor.getStyle()
                        .set("background", "rgba(37,99,235,0.20)")
                        .set("transform", "translateY(-2px)")
                        .set("box-shadow", "0 0 16px rgba(37,99,235,0.18)")
        );
        anchor.getElement().addEventListener("mouseleave", e ->
                anchor.getStyle()
                        .set("background", "rgba(37,99,235,0.10)")
                        .set("transform", "translateY(0)")
                        .set("box-shadow", "none")
        );
        return anchor;
    }

    private void injectMoneyEffects() {
        getUI().ifPresent(ui -> ui.getPage().executeJs("""
                (function () {
                    if (window.moneyFxLoaded) return;
                    window.moneyFxLoaded = true;

                    const style = document.createElement('style');
                    style.textContent = `
                        @keyframes moneyFall {
                            0%   { transform: translateY(-120px) rotate(0deg);   opacity: 0; }
                            10%  { opacity: 0.9; }
                            100% { transform: translateY(120vh) rotate(360deg);  opacity: 0; }
                        }
                        @keyframes moneyBurst {
                            0%   { transform: translate(0,0) scale(0.6) rotate(0deg);                      opacity: 1; }
                            100% { transform: translate(var(--x), var(--y)) scale(1.15) rotate(220deg);    opacity: 0; }
                        }
                        .money-drop {
                            position: fixed;
                            top: -40px;
                            font-size: 18px;
                            pointer-events: none;
                            user-select: none;
                            z-index: 3;
                            filter: drop-shadow(0 0 8px rgba(255,255,255,0.12));
                            animation-name: moneyFall;
                            animation-timing-function: linear;
                            animation-fill-mode: forwards;
                        }
                        .money-burst {
                            position: fixed;
                            left: 0;
                            top: 0;
                            pointer-events: none;
                            user-select: none;
                            z-index: 9999;
                            font-size: 18px;
                            animation: moneyBurst 800ms cubic-bezier(.22,.61,.36,1) forwards;
                            filter: drop-shadow(0 0 10px rgba(255,255,255,0.18));
                        }
                    `;
                    document.head.appendChild(style);

                    const rainHost = document.getElementById('money-rain') || document.body;
                    const moneySet = ['💸', '💵', '💶', '💷'];

                    function spawnDrop() {
                        const el = document.createElement('div');
                        el.className = 'money-drop';
                        el.textContent = moneySet[Math.floor(Math.random() * moneySet.length)];
                        el.style.left = Math.random() * 100 + 'vw';
                        el.style.fontSize = (14 + Math.random() * 14) + 'px';
                        el.style.animationDuration = (6 + Math.random() * 6) + 's';
                        el.style.opacity = 0.85;
                        rainHost.appendChild(el);
                        setTimeout(() => el.remove(), 13000);
                    }

                    for (let i = 0; i < 16; i++) {
                        setTimeout(spawnDrop, i * 350);
                    }
                    setInterval(spawnDrop, 550);

                    let lastBurst = 0;
                    document.addEventListener('pointermove', function (e) {
                        const now = Date.now();
                        if (now - lastBurst < 85) return;
                        lastBurst = now;

                        for (let i = 0; i < 3; i++) {
                            const part = document.createElement('div');
                            part.className = 'money-burst';
                            part.textContent = moneySet[Math.floor(Math.random() * moneySet.length)];
                            part.style.left = e.clientX + 'px';
                            part.style.top  = e.clientY + 'px';
                            part.style.setProperty('--x', ((Math.random() - 0.5) * 120) + 'px');
                            part.style.setProperty('--y', ((Math.random() - 0.5) * 120 - 20) + 'px');
                            part.style.fontSize = (12 + Math.random() * 10) + 'px';
                            document.body.appendChild(part);
                            setTimeout(() => part.remove(), 900);
                        }
                    }, { passive: true });
                })();
                """));
    }

    private BigDecimal sumByDate(List<ExpenseDTO> expenses, LocalDate date) {
        return expenses.stream()
                .filter(e -> e.getDate() != null && e.getDate().isEqual(date))
                .map(ExpenseDTO::getAmount)
                .filter(a -> a != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal sumByMonth(List<ExpenseDTO> expenses, int year, Month month) {
        return expenses.stream()
                .filter(e -> e.getDate() != null)
                .filter(e -> e.getDate().getYear() == year)
                .filter(e -> e.getDate().getMonth() == month)
                .map(ExpenseDTO::getAmount)
                .filter(a -> a != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal sumByYear(List<ExpenseDTO> expenses, int year) {
        return expenses.stream()
                .filter(e -> e.getDate() != null)
                .filter(e -> e.getDate().getYear() == year)
                .map(ExpenseDTO::getAmount)
                .filter(a -> a != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<BigDecimal> buildLast7DaysTotals(List<ExpenseDTO> expenses) {
        LocalDate today = LocalDate.now();
        List<BigDecimal> totals = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            totals.add(sumByDate(expenses, today.minusDays(i)));
        }
        return totals;
    }

    private List<Integer> normalizeBarHeights(List<BigDecimal> values, int minHeight, int maxHeight) {
        if (values == null || values.isEmpty()) {
            return new ArrayList<>();
        }

        BigDecimal max = values.stream()
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ONE);

        if (max.compareTo(BigDecimal.ZERO) == 0) {
            return values.stream().map(v -> minHeight).collect(Collectors.toList());
        }

        return values.stream()
                .map(v -> {
                    double ratio = v.doubleValue() / max.doubleValue();
                    return minHeight + (int) Math.round(ratio * (maxHeight - minHeight));
                })
                .collect(Collectors.toList());
    }

    private String formatMoney(BigDecimal amount) {
        return formatMoneyPlain(amount);
    }

    private String formatMoneyPlain(BigDecimal amount) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(amount == null ? BigDecimal.ZERO : amount) + " so'm";
    }

    private String formatMoneyShort(BigDecimal amount) {
        BigDecimal value = amount == null ? BigDecimal.ZERO : amount;
        double n = value.doubleValue();

        if (n >= 1_000_000_000) {
            return new DecimalFormat("#.#").format(n / 1_000_000_000D) + "B";
        } else if (n >= 1_000_000) {
            return new DecimalFormat("#.#").format(n / 1_000_000D) + "M";
        } else if (n >= 1_000) {
            return new DecimalFormat("#.#").format(n / 1_000D) + "K";
        }
        return new DecimalFormat("#").format(n);
    }

    private String getCategoryEmoji(String category) {
        if (category == null) return "💳";

        return switch (category.trim().toLowerCase(Locale.ROOT)) {
            case "food"          -> "🍔";
            case "transport"     -> "🚕";
            case "education"     -> "📚";
            case "utilities"     -> "💡";
            case "rent"          -> "🏠";
            case "internet"      -> "🌐";
            case "healthcare"    -> "💊";
            case "entertainment" -> "🎮";
            case "clothing"      -> "👕";
            case "travel"        -> "✈️";
            case "gifts"         -> "🎁";
            case "investment"    -> "📈";
            case "savings"       -> "🏦";
            default              -> "💳";
        };
    }

    private String getColorForCategory(String category) {
        if (category == null) return "#94a3b8";

        return switch (category.trim().toLowerCase(Locale.ROOT)) {
            case "food"          -> "#f87171";
            case "transport"     -> "#fb7185";
            case "education"     -> "#38bdf8";
            case "utilities"     -> "#f59e0b";
            case "rent"          -> "#a78bfa";
            case "internet"      -> "#22c55e";
            case "healthcare"    -> "#ef4444";
            case "entertainment" -> "#e879f9";
            case "clothing"      -> "#f97316";
            case "travel"        -> "#14b8a6";
            case "gifts"         -> "#ec4899";
            case "investment"    -> "#10b981";
            case "savings"       -> "#06b6d4";
            default              -> "#94a3b8";
        };
    }
}