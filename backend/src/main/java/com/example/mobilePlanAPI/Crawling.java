package com.example.mobilePlanAPI;

import com.opencsv.CSVWriter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
//import java.util.concurrent.TimeUnit;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class Crawling {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.setProperty("webdriver.chrome.driver", "/home/shubh/Downloads/chromedriver-linux64/chromedriver");

        //Create Chrome instance
        WebDriver driverch = new ChromeDriver();
        driverch.manage().window().maximize();

        // define an implicit wait object of 10 seconds
        WebDriverWait wait = new WebDriverWait(driverch, Duration.ofSeconds(10));

//        //open application and the homepage
//        driverch.get("https://www.bell.ca/");

//        //Locate the navbar mobile button to open a dropdown list
//        WebElement mobile = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("ul[class='topNav'] li:nth-child(2) div[aria-controls='accss-mobile-dd-menu']")));
//        mobile.click();
//
//        // Select the plans option from the dropdown list
//        WebElement plans = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[aria-label='Plans']")));
//        plans.click();
//
//        // main scraping function to locate elements and extract data
//        extract_own_phone_plans(driverch, wait);


        extract_bell(driverch, wait);
        extract_own_phone_plans(driverch, wait);
        extract_telus(driverch, wait);
        driverch.quit();
    }


    public static void extract_telus(WebDriver driver, WebDriverWait wait) throws InterruptedException, IOException {
        //define a csv filepath and name of the file that will be used to store the data
        String filepath = "/home/shubh/Documents/telus_output.csv";
        File file = new File(filepath);

        ArrayList<String[]> datalist = new ArrayList<>();

        //Initialize file writer object to initialize CSVWriter object that is used to write the scraped data into
        FileWriter filewriter = new FileWriter(file);
        CSVWriter writer = new CSVWriter(filewriter);

        writer.writeNext(new String[]{"Title", "Price", "Data", "Features"});

        driver.get("https://www.telus.com/en/");

        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#main-nav-list-item-0"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("div[data-test='reveal-dropdown'] > div:nth-child(1) > div:nth-child(1) > div:nth-child(1) > nav:nth-child(2) > div:nth-child(1) > nav:nth-child(1) > ul:nth-child(1) > li:nth-child(3) > a:nth-child(1)"))).click();

//        driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);

        List<WebElement> containers = driver.findElements(By.cssSelector("div[data-testid='plans-cards-container'] > div:nth-child(1) > div[data-testid='plans-card']"));
        System.out.println(containers.size());
        for(WebElement icontain: containers) {
            String title = icontain.findElement(By.cssSelector("div[data-testid='plans-card-title-container']")).getText();
            String price = icontain.findElement(By.cssSelector("div[data-testid='price-lockup-text']")).getText();
            String amount = icontain.findElement(By.cssSelector("div[data-testid='plans-lockup-data-amount']")).getText();

            System.out.println(title + ":" + price + ":" + amount);
            List<WebElement> featurelist = icontain.findElements(By.cssSelector("div[data-testid='plans-card-features-container'] > div:nth-child(n+4)"));
            ArrayList<String> featureText = new ArrayList<>();
            for(WebElement feature: featurelist) {
                featureText.add(feature.getText());
            }

            System.out.println(title + ":" + price + ":" + amount + ":" + featureText);

            datalist.add(new String[]{title, price, amount, String.valueOf(featureText)});
        }
        writer.writeAll(datalist);
    }

    public static void extract_bell(WebDriver driver, WebDriverWait wait) throws InterruptedException, IOException {
        //define a csv filepath and name of the file that will be used to store the data
        String filepath = "/home/shubh/Documents/bell_output.csv";
        File file = new File(filepath);

        ArrayList<String[]> datalist = new ArrayList<>();

        //Initialize file writer object to initialize CSVWriter object that is used to write the scraped data into
        FileWriter filewriter = new FileWriter(file);
        CSVWriter writer = new CSVWriter(filewriter);

        writer.writeNext(new String[]{"Title", "Data", "Features", "Details", "Price"});

        driver.get("https://www.bell.ca/");

        WebElement mobile = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("li.g-connector-nav-lob:nth-child(1) > button:nth-child(1)")));
        mobile.click();

        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#lobContent-SHOP_MOBILITY > div:nth-child(1) > div:nth-child(1) > div:nth-child(1) > ul:nth-child(2) > li:nth-child(3) > a:nth-child(1)"))).click();


        List<WebElement> slick = driver.findElements(By.cssSelector("div.rateplan-row div.slick-list div.slick-track div.slick-slide"));

        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("div.g-card-plan__details h4 a"))).click();
        for(WebElement slide: slick) {
            String title = slide.findElement(By.cssSelector("h3.g-card-plan__title")).getText();
            String data = slide.findElement(By.cssSelector("div.g-card-plan__data")).getText();
            List<WebElement> featuresWebElement = slide.findElements(By.cssSelector("div.g-card-plan__features ul li"));
            ArrayList<String> features = new ArrayList<>();
            for(WebElement fwe: featuresWebElement) {
                features.add(fwe.getText());
            }


            List<WebElement> detailsWebElement = slide.findElements(By.cssSelector("div.g-card-plan__details div.g-card-plan__details-list ul li"));
            ArrayList<String> details = new ArrayList<>();
            for(WebElement dwe: detailsWebElement) {
                details.add(dwe.getText());
            }

            String price = slide.findElement(By.cssSelector("div.g-card-plan__price")).getText();

            System.out.println(title + ":" + data + ":" + features + ":" + details + ":" + price + ":");
            datalist.add(new String[]{title, data, String.valueOf(features), String.valueOf(details), price});
        }
        writer.writeAll(datalist);
        writer.close();
    }

    // params (WebDriver driver, WebDriverWait wait)
    // driver : driver object used to find elements from a page
    // wait : WebdriverWait object used to implement implicit or explicit wait

    // Throws IOException if no input is found to FileWriter Object
    public static void extract_own_phone_plans(WebDriver driver, WebDriverWait wait) throws IOException {
        //define a csv filepath and name of the file that will be used to store the data
        String filepath = "/home/shubh/Documents/virgin_output.csv";
        File file = new File(filepath);

        //Initialize file writer object to initialize CSVWriter object that is used to write the scraped data into
        FileWriter filewriter = new FileWriter(file);
        CSVWriter writer = new CSVWriter(filewriter);

        // data : Used to store array of string which has plan_fearures and plan_price
        List<String[]> data = new ArrayList<String[]>();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("ratebox-BYOP")));
//        WebElement byop = driver.findElement(By.id("ratebox-BYOP"));
//        Actions action = new Actions(driver);
//        action.moveToElement(byop);

        // Locates the main div element that covers the main scraping area
        List<WebElement> buckets = driver.findElements(By.cssSelector("div.slidenarrow > div"));
        System.out.println(buckets);

        // Each bucket has plan-containers that has multiple plan options
        for( WebElement bucket: buckets) {
            List<WebElement> plan_container = bucket.findElements(By.cssSelector("plan-container"));
            System.out.println(plan_container.size());

            // Each plan-container is used to extract and individual plan and scrape features and price for each plan
            for(WebElement plan: plan_container) {
                List<WebElement> plan_info = plan.findElements(By.cssSelector("div.planInner.tb > div"));

                // extract features and price and convert them to string
                String features = plan_info.get(0).getText();
                String price = plan_info.get(1).getText();

                List<String> csv_data = new ArrayList<String>();

                csv_data.add(features);
                csv_data.add(price);

                String[] benefits = features.split(System.lineSeparator());
                String[] cost = features.split(System.lineSeparator());

                // convert array list to String[] since CSVWriter accepts String[]
                String[] attr = new String[csv_data.size()];
                attr = csv_data.toArray(attr);

                // add each plan information to data variable
                data.add(attr);
            }
        }
        // write all the extracted data at once to the csv file and then close the object
        writer.writeAll(data);
        writer.close();
    }

}

class FreedomDataPlanScraper {

    // Checking if the csv file is empty or not
    boolean isEmptyFile(String d_filename) {
        File file = new File(d_filename);
        return file.length() == 0;
    }

    // Navigating to 4G plan page
    void planScraper4G (WebDriver d_driver) {
        // Locating and clicking the 4G button to load 4G data page
        WebElement d_radioButton4G = d_driver.findElement(By.id("Tablet & Watch"));
        d_radioButton4G.click();
    }

    // Extracting data from webpage and writing into csv file
    void dataExtractor(WebDriver d_driver, String d_csv_file_name, String dataType) {
        // Locating and storing the element consisting the plan detail list
        WebElement d_cardList = d_driver.findElement(By.cssSelector("ul[class*='eiTNuv']"));
        List<WebElement> d_freedomPlanCards = d_cardList.findElements(By.tagName("li"));
        try{
            // Creating an instance of file writer
            FileWriter d_csv_file_writer = new FileWriter(d_csv_file_name,true);
            if (isEmptyFile(d_csv_file_name)) {
                // Applying headers to the csv file
                d_csv_file_writer.append("Price(CAD) / mo, Data, Type, Countries, Calls, Texts\n");
            }
            // Iterating through each list element of plan detail
            for (WebElement d_planCard : d_freedomPlanCards) {
                // Storing the countries included in the plan
                String countryAllowed = dataType == "5G" ? d_planCard.findElement(By.cssSelector("div[class*='bPPpPK'] div[class*='fgTPKE'] p")).getText() : "-";
                // Storing the plan price
                String planPrice = d_planCard.findElement(By.cssSelector("div[class*='dKJmFZ'] div[class*='gWqkZe'] span")).getText();
                // Storing the total provided data
                String totalData = d_planCard.findElement(By.cssSelector("div[class*='bPPpPK'] div[class*='kqjSKX'] h3")).getText();
                // Extracting extra features like allowed calls and texts
                List<WebElement> planInfoList = d_planCard.findElements(By.cssSelector("div[class*='jBnTMb'] p[class*='pDmnc']"));
                String permittedTexts = "-";
                String permittedCalls = "-";
                System.out.println("----------------------------------------------------------------------");
                System.out.println("Price: " + planPrice);
                System.out.println("Data: " + totalData);
                for (WebElement d_planInfoList : planInfoList) {
                    if(d_planInfoList.getText().contains("Unlimited talk & text")){
                        permittedTexts = "Unlimited";
                        permittedCalls = "Unlimited";
                    }
                    else if(d_planInfoList.getText().contains("talk")) {
                        // Extracting allowed call time
                        permittedCalls = d_planInfoList.getText().substring(0, d_planInfoList.getText().lastIndexOf(" "));
                        permittedTexts = "Unlimited";
                    }
                }
                System.out.println("Text: " + permittedTexts);
                System.out.println("Calls: " + permittedCalls);
                System.out.println("----------------------------------------------------------------------");
                // Writing all the collected data into the csv file
                d_csv_file_writer.append("\"").append(planPrice).append("\",\"").append(totalData).append("\",\"").append(dataType).append("\",\"").append(countryAllowed).append("\",\"").append(permittedCalls).append("\",\"").append(permittedTexts).append("\"\n");
            }
            // Closing the csv writer
            d_csv_file_writer.close();
        }
        catch (IOException e){
            // Handling exception
            e.printStackTrace();
        }
    }

    void d_scraper (String d_url, String d_csv_file_name) {
        // Creating instance of chromedriver
        WebDriver d_driver = new ChromeDriver();
        // Navigating to the requested URL
        d_driver.get(d_url);
        System.out.println("Website opened successfully");
        // Waiting for 10 sec to ensure complete loading of page
//        d_driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        // Maximizing chrome window
        d_driver.manage().window().maximize();

        // Pointing to mobile element and clicking
        WebElement d_mobile_button = d_driver.findElement(By.cssSelector("#desktop-header-mobile"));
        d_mobile_button.click();

        // Pointing to plan element and clicking
        WebElement d_plan_link = d_driver.findElement(By.cssSelector("#iw2ftbbslvsmg3yseixvy"));
        d_plan_link.click();

        // Pointing to 'bring your own device' element and clicking
        WebElement d_bringYourOwn_link = d_driver.findElement(By.cssSelector("div[data-testid='header-dropdown-stage-column-2-item-bring-your-own-phone-menuitem']"));
        d_bringYourOwn_link.click();

        // Extracting data for 5G page
        dataExtractor(d_driver, d_csv_file_name, "5G");
        // Calling the 4G page
        planScraper4G(d_driver);
        // Extracting data for 4G page
        dataExtractor(d_driver, d_csv_file_name, "4G");

        // Closing the browser
        d_driver.quit();
    }
    public static void main(String args[]){
        // Setting up the chromedriver
        System.setProperty("webdriver.chrome.driver", "E:\\Software SetUp\\chromedriver-win64\\chromedriver-win64\\chromedriver.exe");

        // Creating instance of FreedomDataPlanScraper
        FreedomDataPlanScraper d_freedomDataScraper = new FreedomDataPlanScraper();
        // Calling the scraper function providing URL and CSV file name
        d_freedomDataScraper.d_scraper("https://www.freedommobile.ca/en-CA", "Data_Plan_Freedom.csv");
    }
}
