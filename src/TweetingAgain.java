import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JLabel;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.awt.GridLayout;
import org.eclipse.wb.swing.FocusTraversalOnArray;
import java.awt.Component;
import javax.swing.SwingConstants;
import javax.swing.JSeparator;

public class TweetingAgain extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private int neg = 0, pos = 0, neu = 0, tot = 0;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;

	/**
	 * Launch the application.
	 */

	public void maining(String string) throws IOException, TwitterException {

		// Your Twitter App's Consumer Key
		String consumerKey = "ecGHfsMwx38UCNC6CbQI3w";

		// Your Twitter App's Consumer Secret
		String consumerSecret = "4kBTO8pwa7XpwdoIEGKkNTUdHODOe4TywXKgBXuEE";

		// Your Twitter Access Token
		String accessToken = "168741775-mQjCUa9TrRajOkWqmgatZZ7vxFPUxFzIQwXfZdYm";

		// Your Twitter Access Token Secret
		String accessTokenSecret = "JiRAZnqqJw75CiwohFrDo7yswkXc0IR7Tsz6I73lo";

		// Instantiate a re-usable and thread-safe factory
		TwitterFactory twitterFactory = new TwitterFactory();

		// Instantiate a new Twitter instance
		Twitter twitter = twitterFactory.getInstance();

		// setup OAuth Consumer Credentials
		twitter.setOAuthConsumer(consumerKey, consumerSecret);

		// setup OAuth Access Token
		twitter.setOAuthAccessToken(new AccessToken(accessToken,
				accessTokenSecret));

		Query query = new Query(textField.getText().toString());
		query.setCount(100);
		try {
			SentimentClassifier sentClassifier = new SentimentClassifier();
			int count = 0;
			QueryResult r;
			do {
				r = twitter.search(query);
				ArrayList ts = (ArrayList) r.getTweets();

				for (int i = 0; (i < ts.size()) && (count < 500); i++) {
					tot++;
					count++;
					Status t = (Status) ts.get(i);
					String text = t.getText();
					String name = t.getUser().getScreenName();
					System.out.println("User: " + name);
					System.out.println("Text: " + text);
					String sent = sentClassifier.classify(t.getText());
					System.out.println("Sentiment: " + sent);

					if (sent.equals("pos"))
						pos++;
					else if (sent.equals("neu"))
						neu++;
					else
						neg++;
				}
			} while ((query = r.nextQuery()) != null && count < 500);
			textField_1.setText(pos + "");
			textField_2.setText(neg + "");
			textField_3.setText(neu + "");
			JFrame f = new JFrame("Pie Chart");
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			int y[] = new int[3];

			y[0] = neg;
			y[1] = pos;
			y[2] = neu;

			f.getContentPane().add(new PieChartPanel(y));
			f.setSize(350, 300);
			f.setLocation(300, 300);
			f.setVisible(true);

		} catch (Exception e) {
			System.out.println(e + " ");
		}

	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TweetingAgain frame = new TweetingAgain();
					frame.setVisible(true);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public TweetingAgain() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.NORTH);
		GridLayout gl_panel = new GridLayout(5, 5);
		gl_panel.setVgap(10);
		gl_panel.setHgap(10);
		panel.setLayout(gl_panel);

		JLabel lblWhatToSearch = new JLabel("What to search");
		lblWhatToSearch.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lblWhatToSearch);

		JButton btnGo = new JButton("Go");
		btnGo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					maining("zxnklxzcm");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (TwitterException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		textField = new JTextField();
		panel.add(textField);
		panel.setFocusTraversalPolicy(new FocusTraversalOnArray(
				new Component[] { lblWhatToSearch, textField, btnGo,
						textField_1 }));
		panel.add(btnGo);

		JSeparator separator = new JSeparator();
		panel.add(separator);

		JLabel lblNewLabel = new JLabel("Positive Responses:");
		panel.add(lblNewLabel);

		textField_1 = new JTextField();
		panel.add(textField_1);

		JLabel lblNegativeResponses = new JLabel("Negative Responses:");
		panel.add(lblNegativeResponses);

		textField_2 = new JTextField();
		panel.add(textField_2);
		textField_2.setColumns(10);

		JLabel lblNeutralResponses = new JLabel("Neutral Responses:");
		panel.add(lblNeutralResponses);

		textField_3 = new JTextField();
		panel.add(textField_3);
		textField_3.setColumns(10);
	}

}

class PieChartPanel extends JPanel {
	BufferedImage image;
	final int PAD = 30;
	Font font;
	NumberFormat numberFormat;
	int[] score = { 98, 85, 20 };

	public PieChartPanel() {
		font = new Font("Book Antiqua", Font.BOLD, 20);
		numberFormat = NumberFormat.getPercentInstance();
		addComponentListener(new ComponentAdapter() {
		});
	}

	public PieChartPanel(int y[]) {
		score = y;
		font = new Font("Book Antiqua", Font.BOLD, 20);
		numberFormat = NumberFormat.getPercentInstance();
		addComponentListener(new ComponentAdapter() {
		});
	}

	protected void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		Graphics2D graphics2d = (Graphics2D) graphics;
		graphics2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		createChartImage();
		graphics2d.drawImage(image, 0, 0, this);
	}

	private void createChartImage() {
		int width = getWidth();
		int height = getHeight();
		int cp = width / 2;
		int cq = height / 2;
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = image.createGraphics();
		g2.setPaint(Color.WHITE);
		g2.fillRect(0, 0, width, height);
		g2.setPaint(Color.black);
		int pie = Math.min(width, height) - 2 * PAD;
		g2.draw(new Ellipse2D.Double(cp - pie / 2, cq - pie / 2, pie, pie));
		double total = 0;
		for (int j = 0; j < score.length; j++)
			total += score[j];
		double theta = 0, phi = 0;
		double p, q;
		for (int j = 0; j < score.length; j++) {
			p = cp + (pie / 2) * Math.cos(theta);
			q = cq + (pie / 2) * Math.sin(theta);
			g2.draw(new Line2D.Double(cp, cq, p, q));
			phi = (score[j] / total) * 2 * Math.PI;
			p = cp + (9 * pie / 24) * Math.cos(theta + phi / 2);
			q = cq + (9 * pie / 24) * Math.sin(theta + phi / 2);
			g2.setFont(font);
			String st = String.valueOf(score[j]);
			FontRenderContext frc = g2.getFontRenderContext();
			float textWidth = (float) font.getStringBounds(st, frc).getWidth();
			LineMetrics lm = font.getLineMetrics(st, frc);
			float sp = (float) (p - textWidth / 2);
			float sq = (float) (q + lm.getAscent() / 2);
			g2.drawString(st, sp, sq);
			p = cp + (pie / 2 + 4 * PAD / 5) * Math.cos(theta + phi / 2);
			q = cq + (pie / 2 + 4 * PAD / 5) * Math.sin(theta + phi / 2);
			st = numberFormat.format(score[j] / total);
			textWidth = (float) font.getStringBounds(st, frc).getWidth();
			lm = font.getLineMetrics(st, frc);
			sp = (float) (p - textWidth / 2);
			sq = (float) (q + lm.getAscent() / 2);
			String pr = null;
			if (j == 0) {
				pr = "neg";
			} else {
				if (j == 1) {
					pr = "pos";
				} else {
					pr = "neu";
				}
			}
			g2.drawString(pr + " : "+st, sp, sq);
			theta += phi;
		}
		g2.dispose();
	}

	public void toggleVisibility() {
		repaint();
	}
}
