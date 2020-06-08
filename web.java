import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.Keys;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import org.openqa.selenium.remote.DesiredCapabilities;
import java.awt.AWTException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class web {
    public static ChromeDriver driver;
    public static String chrome_path = "./chromedriver";
    public static String link = "https://web.whatsapp.com/";
    public static String extension_path = "./InTouchAppPhoneContactsDataSaver.crx";
    public static String menu_path = "./menu.csv";
    public static String csv_path = "./test.csv";
    //public static Actions a;
    public static int count =0;
    public static ArrayList<menu> menu_list = new ArrayList<menu>();

    public static class menu{
        public String item_name;
        public String item_cost;

        menu(String item_name, String item_cost){
            this.item_name = item_name;
            this.item_cost = item_cost;
        }

    }

    public static void initialization() throws InterruptedException, IOException {
        String[] menu_header;
        String menu_row;

        //To read the Menu and store in data
        BufferedReader menu_csvReader = new BufferedReader(new FileReader(menu_path));
        menu_header = menu_csvReader.readLine().split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
        while ((menu_row = menu_csvReader.readLine())!= null ){
            String[] data = menu_row.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
            menu item = new menu(data[0], data[1]);
            menu_list.add(item);
        }
        menu_csvReader.close();

        
        System.setProperty("webdriver.chrome.driver", chrome_path);
        // WebDriver driver = new ChromeDriver();
        ChromeOptions options = new ChromeOptions();
        options.addExtensions(new File(extension_path));
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(ChromeOptions.CAPABILITY, options);
        driver = new ChromeDriver(capabilities);
        //a = new Actions(driver);
        driver.get(link);

        /* Follow code meant to give instruction on the terminal */
        Scanner myObj = new Scanner(System.in);
        System.out.println("STEP 1: Scan Whatsapp QR code \n STEP 2: Click 'Later' on Popup \n STEP 3: Click Red Extension \n STEP 4: Scan InTouchApp QR code & remove pop up by clicking else where\n STEP 5: Press Enter on Terminal");
        myObj.nextLine();
        Thread.sleep(2000);
        myObj.close();
            
        }


    public static void automate() throws InterruptedException, IOException, AWTException {
        String[] header;
        String row;

        // To Read the Order Sheet
        BufferedReader csvReader = new BufferedReader(new FileReader(csv_path));
        header = csvReader.readLine().split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
        while ((row = csvReader.readLine()) != null) {
            //Extracting data off of each line
            String[] data = row.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
        
            //Taking the important details, such as 1) Name 2) Phone Number 3) Order History
            String name = data[Arrays.asList(header).indexOf("Sender's Name")];
            String phone_number = data[Arrays.asList(header).indexOf("☎️ Phone Number")];
            String order_history = data[Arrays.asList(header).indexOf("Is this your first time ordering from tiaptiapwithsoph?")];
            //System.out.println(order_history);
            //Double checking for name and phone_number
            System.out.println(name);
            System.out.println(phone_number);
            String final_message = templating(data, header);
            
            //System.out.println(final_message); //UN-COMMENT IF YOU WANT TO TEST MESSGAE BEFORE SENDING
            
            // Separating between old and new contacts     
            if(order_history.equals("Yes")){
                System.out.println("Creating New contact");
                sendMessage_new(name, phone_number, final_message);   //UN-COMMENT WHEN YOU WANT TO SEND
            }

            else if(order_history.equals("No")){
                System.out.println("Searching through old contacts");
                sendMessage_old(name, phone_number, final_message);
            }
            count++;
        }
        csvReader.close();
    }

    public static void sendMessage_new(String name, String phone_number, String message)
            throws InterruptedException, AWTException {

        //System.out.println("test");

        WebElement addContact = driver.findElement(By.xpath("/html/body/div[1]/div[1]/div/div[3]/div/header/div[2]/div/span/div[2]/div/span[1]/img"));
        addContact.click();
        Thread.sleep(1000);
        
        WebElement addNewContact = driver.findElement(By.xpath("/html/body/div[1]/div[1]/div/div[3]/div/header/div[2]/div/span/div[2]/div/span[2]/div/ul/li[1]"));
        addNewContact.click();
        Thread.sleep(1000);
        
        WebElement insertName = driver.findElement(By.xpath("//*[@id='addContactExternalForm']/div/div[1]/input"));
        insertName.sendKeys(name);
        Thread.sleep(1000);

        WebElement insertNumber = driver.findElement(By.xpath("//*[@id='addContactExternalForm']/div/div[4]/input"));
        insertNumber.sendKeys(phone_number);
        Thread.sleep(1000);
        
        WebElement saveChat = driver.findElement(By.xpath("//*[@id='addIntouchContact']"));
        saveChat.click();
        Thread.sleep(5000);

        WebElement insertText = driver.findElement(By.xpath("/html/body/div[1]/div[1]/div/div[4]/div[3]/footer/div[1]/div[2]/div/div[2]"));
        String[] message_split = message.split("\n");
        for(String i : message_split ){
            insertText.sendKeys(i);
            insertText.sendKeys(Keys.chord(Keys.SHIFT, Keys.ENTER));
        }
        Thread.sleep(2000);
        
        //UNCOMMENT BOTTOM 2 LINES OF CODE IF YOU WANT TO CHECK EACH MESSAGE
        //Scanner myObj2 = new Scanner(System.in);
        //myObj2.nextLine();
        //UNCOMMENT TOP 2 LINES OF CODE IF YOU WANT TO CHECK EACH MESSAGE

        WebElement sendChat = driver.findElement(By.xpath("/html/body/div[1]/div[1]/div/div[4]/div[3]/footer/div[1]/div[3]/button"));
        sendChat.click();
        Thread.sleep(5000);
        

    }

    public static void sendMessage_old(String name, String phone_number, String message)
            throws InterruptedException, AWTException {

        WebElement searchContact = driver.findElement(By.xpath("//*[@id='side']/div[4]/div/label/div/div[2]"));
        searchContact.sendKeys(phone_number);
        Thread.sleep(4000);
        

        WebElement selectContact = driver.findElement(By.xpath("//*[@id='pane-side']/div[1]/div/div/div[1]/div/div"));
        selectContact.click();
        Thread.sleep(1000);
        
        WebElement insertText = driver.findElement(By.xpath("/html/body/div[1]/div[1]/div/div[4]/div[3]/footer/div[1]/div[2]/div/div[2]"));
        String[] message_split = message.split("\n");
        for(String i : message_split ){
            insertText.sendKeys(i);
            insertText.sendKeys(Keys.chord(Keys.SHIFT, Keys.ENTER));
        }
        Thread.sleep(2000);
        
        //UNCOMMENT BOTTOM 2 LINES OF CODE IF YOU WANT TO CHECK EACH MESSAGE
        //Scanner myObj2 = new Scanner(System.in);
        //myObj2.nextLine();
        //UNCOMMENT TOP 2 LINES OF CODE IF YOU WANT TO CHECK EACH MESSAGE

        WebElement sendChat = driver.findElement(By.xpath("/html/body/div[1]/div[1]/div/div[4]/div[3]/footer/div[1]/div[3]/button"));
        sendChat.click();
        Thread.sleep(2000);
        

    }

    public static String templating(String[] data, String[] header) {
        //initialising all the variables
        String name, qty, item_name, price,address, delivery_cost, delivery_date, delivery_date_buffer, message_option, message,recipient_name, delivery_type, orderline,total_cost, final_message;
        
        //assinging values to variables in order of template
        name = data[Arrays.asList(header).indexOf("Sender's Name")];
        recipient_name = data[Arrays.asList(header).indexOf("Recipient's Name")];
        orderline = "";
        address = data[Arrays.asList(header).indexOf("🏡Address")] + " " + data[Arrays.asList(header).indexOf("📍Postal Code")]; 
        delivery_cost = data[Arrays.asList(header).indexOf("Delivery Cost")];
        if(delivery_cost.equals("")){
            delivery_cost = "0";
        }
        delivery_date_buffer = data[Arrays.asList(header).indexOf("🚚 Available Dates")];
        delivery_date = delivery_date_buffer.substring(1,delivery_date_buffer.length()-1);
        total_cost = data[Arrays.asList(header).indexOf("Amount Payable")];
        message_option=data[Arrays.asList(header).indexOf("📝 Message")]; //YOU MIGHT NEED TO CHANGE THIS TO "📝 Would you like to include a Message with this purchase?"
        message = data[Arrays.asList(header).indexOf("Message to be Included:")];
        delivery_type = data[Arrays.asList(header).indexOf("Transport")]; //NOTE: DELIVERY TYPE IS CURRENTLY (as of 030620) NOT IN THE MESSAGE

        //getting the orderline
        for (int i = 0; i < menu_list.size(); i++){
            //System.out.println(menu_list.get(i).item_name);
            int index = Arrays.asList(header).indexOf(menu_list.get(i).item_name);
            if(data[index].isEmpty() || data[index].equals("0")){
                continue;
            }
            else{
                qty = data[index];
                price = menu_list.get(i).item_cost;
                item_name = header[index];
                orderline += (qty + " * " + item_name + " $" + price + "\n");
            }    
        }
        final_message = "";
        // String temp = String.format("THIS IS A TESTER MESSAGE PLEASE IGNORE \n Thank you for ordering with TiapTiapWithSoph, here is your order summary 🥳 - \n\nName:\n%s \n\n📆 Time of Order:\n%s \n\n📝 Order:\n%s\n\n🏡 Delivery Address:\n%s\n\n🚚 Delivery cost & date:\n$%s,%s\n\n💵 Total cost:\n%.2f\nPlease make your payment to 90089066 via paylah or paynow. Once you have made the payment, please send a screenshot to the number together with this order form to complete the order process 🎉\n",name,time_stamp,orderline,address,delivery_cost,delivery_date,total_cost);
        if(message_option.equals("Yes")){
            final_message = String.format("Hey there! Thank you for ordering with TiapTiapWithSoph <3, here is your order summary - \n\nSender's Name:\n%s \n\nRecipient's Name:\n%s \n\nOrder:\n%s\n\nDelivery Address:\n%s\n\nDelivery cost & date:\n$%s,%s\n\nTotal cost:\n$%s\n\nMessage:\n%s\n\nPlease make your payment to 90089066 via paylah or paynow. You can also bank transfer to POSB 051160410. Once you have made the payment, please send a screenshot to the number together with this order form to complete the order process <3 \n",name,recipient_name,orderline,address,delivery_cost,delivery_date,total_cost,message);
        }
        else if(message_option.equals("No")){
            final_message = String.format("Hey there! Thank you for ordering with TiapTiapWithSoph <3, here is your order summary - \n\nRecipient's Name:\n%s \n\nOrder:\n%s\n\nDelivery Address:\n%s\n\nDelivery cost & date:\n$%s,%s\n\nTotal cost:\n$%s\n\nPlease make your payment to 90089066 via paylah or paynow. You can also bank transfer to POSB 051160410. Once you have made the payment, please send a screenshot to the number together with this order form to complete the order process <3 \n",name,orderline,address,delivery_cost,delivery_date,total_cost);
        }
        //String template = "Name:\n{name}\n📆 Time of Order:\n{time_stamp}\n📝 Order:\n{qty} * (item_name) $(price)\n🏡 Delivery Address:\n{address}\n🚚 Delivery cost:\n{delivery_cost}\n💵 Total cost:\n{total_cost}\nPlease make your payment to 90089066 via paylah or paynow. Once you have made the payment, please send a screenshot to the number together with this order form to complete the order process 🎉";
        return final_message;
    }
    
    public static void main(String[] args) throws InterruptedException, IOException, AWTException {
        initialization();
        automate();
        System.out.println("SUCCESS");

    }    
}