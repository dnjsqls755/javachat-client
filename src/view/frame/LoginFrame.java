package view.frame;

import app.Application;
import domain.User;
import dto.request.LoginRequest;
import network.MessageSender;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// [추가 import]
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

public class LoginFrame extends JFrame implements ActionListener {
    LobbyFrame lobbyFrame;

    JLabel idLabel = new JLabel("아이디 ");
    JLabel pwLabel = new JLabel("비밀번호");
    JTextField idTextF = new JTextField(20);
    JPasswordField pwTextF = new JPasswordField(20);
    JButton loginBtn = new JButton("로그인");
    JButton joinBtn = new JButton("회원가입"); // 회원가입 버튼 추가

    // [추가] 당일 날짜 배지 아이콘 라벨 + 마지막 날짜 캐싱
    private final JLabel dateBadgeLabel = new JLabel();

    private final JLabel weatherImageLabel = new JLabel();

    private LocalDate lastDate = LocalDate.now();

    public LoginFrame(LobbyFrame lobbyFrame) {
        this.lobbyFrame = lobbyFrame;

        setLayout(null);

        idLabel.setBounds(100, 50, 100, 50);
        add(idLabel);
        idTextF.setBounds(200, 50, 300, 50);
        add(idTextF);

        pwLabel.setBounds(100, 120, 100, 50);
        add(pwLabel);
        pwTextF.setBounds(200, 120, 300, 50);
        add(pwTextF);

        loginBtn.setBounds(150, 220, 140, 50);
        loginBtn.addActionListener(this);
        add(loginBtn);

        joinBtn.setBounds(310, 220, 140, 50); // 회원가입 버튼 위치 설정
        joinBtn.addActionListener(this);
        add(joinBtn);

        // [추가] 날짜 배지 라벨 배치(우측 상단)
        dateBadgeLabel.setBounds(520, 10, 64, 64); // 위치/크기 필요 시 조정
        add(dateBadgeLabel);

        // [추가] 최초 표시
        dateBadgeLabel.setIcon(new ImageIcon(CalendarBadge.createBadge(LocalDate.now(), 64, 64)));

        // 날씨 이미지 라벨 (우측 상단)
        weatherImageLabel.setBounds(520, 80, 64, 64);
        add(weatherImageLabel);

        // 날씨 API 호출 및 이미지 설정
        String apiKey = "1e9136088e03c34b29269b0e130d17a1"; // 발급받은 API 키
        String weather = WeatherFetcher.getWeatherCondition("Seoul", apiKey);
        weatherImageLabel.setIcon(getWeatherIcon(weather));


        // [추가] 자정 이후 자동 갱신(1분마다 날짜 변화만 체크)
        new javax.swing.Timer(60_000, e -> {
            LocalDate today = LocalDate.now();
            if (!today.equals(lastDate)) {
                lastDate = today;
                dateBadgeLabel.setIcon(new ImageIcon(CalendarBadge.createBadge(today, 64, 64)));
            }
        }).start();

        setSize(600, 400);
        setVisible(true);
    }

    
    private ImageIcon getWeatherIcon(String weather) {
        switch (weather) {
            case "Clear":
                return new ImageIcon(new ImageIcon("image/sun.png").getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH));
            case "Clouds":
                return new ImageIcon(new ImageIcon("image/cloud.png").getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH));
            case "Rain":
                return new ImageIcon(new ImageIcon("image/rain.png").getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH));
            default:
                return new ImageIcon(new ImageIcon("image/default.png").getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH));
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginBtn) {
            String id = idTextF.getText();
            String pw = new String(pwTextF.getPassword());

            if (id.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "user id is empty.", "Message", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (pw.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "user name is empty.", "Message", JOptionPane.ERROR_MESSAGE);
                return;
            }

            User user = new User(id, pw);
            Application.me = user;
            Application.users.add(user);
            Application.sender.sendMessage(new LoginRequest(id, pw));

            this.dispose();
            lobbyFrame.setVisible(true);
        }

        if (e.getSource() == joinBtn) {
            new JoinFrame(); // 회원가입 창 열기
        }
    }

    // ------------------------------------------------------------------------
    //   당일 날짜 배지 이미지를 직접 그리는 유틸 (정적 내부 클래스)
    // - 외부 API 없이 BufferedImage + Graphics2D로 렌더링
    // - 색상/폰트/크기 원하는 대로 커스터마이즈 가능
    // ------------------------------------------------------------------------
    private static final class CalendarBadge {

        /** 임의 날짜로 배지 생성 */
        static BufferedImage createBadge(LocalDate date, int width, int height) {
            BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = img.createGraphics();
            try {
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                // 배경 그라디언트
                GradientPaint gp = new GradientPaint(0, 0, new Color(245, 247, 250),
                                                     0, height, new Color(230, 234, 240));
                g.setPaint(gp);
                g.fill(new RoundRectangle2D.Float(0, 0, width, height, 22, 22));

                // 상단 헤더(월)
                int headerH = Math.max(22, height / 5);
                g.setColor(new Color(220, 65, 65)); // 캘린더 느낌 레드
                g.fill(new RoundRectangle2D.Float(0, 0, width, headerH, 22, 22));
                g.fillRect(0, headerH - 10, width, 10);

                Locale ko = Locale.KOREAN;
                String monthText = date.getMonth().getDisplayName(TextStyle.SHORT, ko);  // "10월" 등
                String dowText   = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, ko); // "일" 등
                String yearText  = String.valueOf(date.getYear());
                String dayText   = String.valueOf(date.getDayOfMonth());

                // 상단 월 텍스트
                g.setColor(Color.WHITE);
                Font headerFont = fontOrFallback("Malgun Gothic", Font.BOLD, Math.max(12, headerH - 8));
                g.setFont(headerFont);
                drawCentered(g, monthText, new Rectangle(0, 0, width, headerH));

                // 본문: 날짜(큰 숫자)
                Font dayFont = fontOrFallback("Malgun Gothic", Font.BOLD, Math.max(28, height / 2));
                g.setFont(dayFont);
                Rectangle dayArea = new Rectangle(0, headerH, width, height - headerH - (height / 6));

                // 그림자
                g.setColor(new Color(0, 0, 0, 40));
                drawCentered(g, dayText, new Rectangle(dayArea.x, dayArea.y + 2, dayArea.width, dayArea.height));
                g.setColor(new Color(40, 40, 40));
                drawCentered(g, dayText, dayArea);

                // 하단: 요일 · 연도
                Font subFont = fontOrFallback("Malgun Gothic", Font.PLAIN, Math.max(12, height / 8));
                g.setFont(subFont);
                g.setColor(new Color(90, 90, 90));
                String sub = dowText + " · " + yearText;
                drawCentered(g, sub, new Rectangle(0, height - (height / 6), width, (height / 6)));

            } finally {
                g.dispose();
            }
            return img;
        }

        /** 폰트가 없으면 시스템 폴백으로 대체 */
        private static Font fontOrFallback(String name, int style, int size) {
            try {
                return new Font(name, style, size);
            } catch (Exception e) {
                return new Font(Font.SANS_SERIF, style, size);
            }
        }

        /** 텍스트를 영역 중앙에 그리기 */
        private static void drawCentered(Graphics2D g, String text, Rectangle area) {
            FontMetrics fm = g.getFontMetrics();
            int textW = fm.stringWidth(text);
            int textH = fm.getAscent() - fm.getDescent();
            int x = area.x + (area.width - textW) / 2;
            int y = area.y + (area.height + textH) / 2;
            g.drawString(text, x, y);
        }
    }
}
