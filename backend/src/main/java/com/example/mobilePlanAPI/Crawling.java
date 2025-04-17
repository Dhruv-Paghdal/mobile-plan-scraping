package com.example.mobilePlanAPI;

import com.opencsv.CSVWriter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Crawling {

    Crawling() {
        System.setProperty("webdriver.chrome.driver", "/home/shubh/Downloads/chromedriver-linux64/chromedriver");
    }
    public JSONObject getPlans() throws IOException, InterruptedException {

        WebDriver driverch = getWebDriver();

        // define an implicit wait object of 10 seconds
        WebDriverWait wait = new WebDriverWait(driverch, Duration.ofSeconds(20));

        JSONObject result = new JSONObject();
        result.put("Bell", extract_bell(driverch, wait));
        driverch.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
        result.put("Virgin", extract_virgin(driverch, wait));
        driverch.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
        result.put("Telus", extract_telus(driverch, wait));
        driverch.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
        result.put("Rogers", extract_rogers(driverch, wait));
        driverch.quit();
        return result;
    }

    private static WebDriver getWebDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--ignore-certificate-errors");
        options.addArguments("--allow-running-insecure-content");
        options.addArguments("--proxy-server='direct://'");
        options.addArguments("--proxy-bypass-list=*");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        String user_agent = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.6998.35 Safari/537.36";
        options.addArguments(String.format("user-agent=%s", user_agent));
        //Create Chrome instance
        WebDriver driverch = new ChromeDriver(options);
        return driverch;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        System.setProperty("webdriver.chrome.driver", "/home/shubh/Downloads/chromedriver-linux64/chromedriver");

        //Create Chrome instance
        WebDriver driverch = new ChromeDriver();
        driverch.manage().window().maximize();

        // define an implicit wait object of 10 seconds
        WebDriverWait wait = new WebDriverWait(driverch, Duration.ofSeconds(20));

        extract_bell(driverch, wait);
        driverch.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
        extract_virgin(driverch, wait);
        driverch.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
        extract_telus(driverch, wait);
        driverch.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
        extract_rogers(driverch, wait);
        driverch.quit();
    }

    public static String getTitle(String text) {
        Pattern pattern = Pattern.compile("[3-5]G\\+?|Voice");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find())
        {
            return text.substring(matcher.start(), matcher.end());
        }
        return "";
    }

    public static String getAmountPerDay(String text) {
        String[] args = text.split(" ");
        if(args.length > 1)
            return args[1];
        return "";
    }

    public static String getCountries(String text) {
        String[] args = text.split(" ");
        if(args.length > 2)
            return args[2];
        return "";
    }

    public static JSONArray extract_telus(WebDriver driver, WebDriverWait wait) throws InterruptedException, IOException {
        //define a csv filepath and name of the file that will be used to store the data

        System.out.println("Telus data extration starting...");
        String filepath = "/home/shubh/Documents/telus_output.csv";
        File file = new File(filepath);

        ArrayList<String[]> datalist = new ArrayList<>();
        JSONArray array = new JSONArray();
        //Initialize file writer object to initialize CSVWriter object that is used to write the scraped data into
        FileWriter filewriter = new FileWriter(file);
        CSVWriter writer = new CSVWriter(filewriter);

        writer.writeNext(new String[]{"Type", "Price", "Features", "Amount"});

        driver.get("https://www.telus.com/en/mobility/plans?linkname=Plans&linktype=ge-meganav");

//        WebElement container = driver.findElement(By.cssSelector("div[data-testid='plan-cards-container']"));

        System.out.println("Telus data extraction started");

        try {
            List<WebElement> cards = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector(".component_cardContainer__yuogS")));
            for (WebElement icontain : cards) {
                JSONObject json = new JSONObject();

                String title = icontain.findElement(By.cssSelector("div[data-testid='plans-card-title-container']")).getText();
                String type = getTitle(title);
                json.put("type", type);
                String amountOfData = getAmountPerDay(title);
                json.put("amountOfData", amountOfData);
                String countries = getCountries(title);
                json.put("countries", countries);
                String price = icontain.findElement(By.cssSelector("div[data-testid='price-lockup-text']")).getText();
                json.put("price", price);
                String amount;
                try {
                    amount = icontain.findElement(By.cssSelector("div[data-testid='price-lockup-data-amount']")).getText();
                } catch (NoSuchElementException | StaleElementReferenceException e) {
                    amount = "Does not exist";
                }
                json.put("amount", amount);

                List<WebElement> featurelist = icontain.findElements(By.cssSelector("div[data-testid='plans-card-features-container'] > div:nth-child(n+4)"));
                ArrayList<String> featureText = new ArrayList<>();
                for (WebElement feature : featurelist) {
                    featureText.add(feature.getText());
                }
                json.put("features", featureText);
                
                datalist.add(new String[]{title, price, String.valueOf(featureText), amount});

                array.put(json);
            }
            writer.writeAll(datalist);
            writer.close();
            System.out.println("Telus data extraction finished");
            System.out.println(array);
        } catch (Exception e) {
            System.out.println("Some error occurred while scraping Telus");
            System.out.println(e);
        }
        return array;
    }

    public static String getDataBell(String text) {
        String itext = text.split("footnote")[0];
        return itext.split("at")[0];
    }

    public static String getDataTypeBell(String text) {
        String itext = text.split("footnote")[0];
        return itext.split("at")[1];
    }

    public static JSONArray extract_bell(WebDriver driver, WebDriverWait wait) throws InterruptedException, IOException {
        //define a csv filepath and name of the file that will be used to store the data

        System.out.println("Bell data extraction starting...");

        String filepath = "/home/shubh/Documents/bell_output.csv";
        File file = new File(filepath);

        ArrayList<String[]> datalist = new ArrayList<>();
        JSONArray array = new JSONArray();

        //Initialize file writer object to initialize CSVWriter object that is used to write the scraped data into
        FileWriter filewriter = new FileWriter(file);
        CSVWriter writer = new CSVWriter(filewriter);

        writer.writeNext(new String[]{"Title", "Data", "Features", "Countries", "Details", "Price"});

        driver.get("https://www.bell.ca/");

//        // Take a screenshot and save it to a file
//        TakesScreenshot screenshot = (TakesScreenshot) driver;
//        File srcFile = screenshot.getScreenshotAs(OutputType.FILE); // Take screenshot
//
//        // Specify the destination file path
//        File destFile = new File("/home/shubh/Desktop/2.png");
//
//        // Use FileUtils to save the file to the specified location
//        FileUtils.copyFile(srcFile, destFile);

        System.out.println("Bell data extraction started");

        try {
            WebElement mobile = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("li.g-connector-nav-lob:nth-child(1) > button:nth-child(1)")));
            mobile.click();

            wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#lobContent-SHOP_MOBILITY > div:nth-child(1) > div:nth-child(1) > div:nth-child(1) > ul:nth-child(2) > li:nth-child(3) > a:nth-child(1)"))).click();

            wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#enhance-tab-2"))).click();

            List<WebElement> slick = driver.findElements(By.cssSelector("div.rateplan-row div.slick-list div.slick-track div.slick-slide"));

            wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("div.g-card-plan__details h4 a"))).click();
            for (WebElement slide : slick) {
                JSONObject json = new JSONObject();

                String title = slide.findElement(By.cssSelector("h3.g-card-plan__title")).getText();
                String data = slide.findElement(By.cssSelector("div.g-card-plan__data")).getText();
                String type = getDataTypeBell(data);
                String dataamount = getDataBell(data);
                json.put("dataamount", dataamount);
                json.put("type", type);
                List<WebElement> featuresWebElement = slide.findElements(By.cssSelector("div.g-card-plan__features ul li"));
                ArrayList<String> features = new ArrayList<>();
                for (WebElement fwe : featuresWebElement) {
                    features.add(fwe.getText().split("\n")[0]);
                }
                json.put("features", features);

                String countries = featuresWebElement.get(0).getText().split("footnote")[0];
                json.put("countries", countries);

                List<WebElement> detailsWebElement = slide.findElements(By.cssSelector("div.g-card-plan__details div.g-card-plan__details-list ul li"));
                ArrayList<String> details = new ArrayList<>();
                for (WebElement dwe : detailsWebElement) {
                    details.add(dwe.getText().split("\n")[0]);
                }
                json.put("details", details);

                String price = slide.findElement(By.cssSelector("div.g-card-plan__price")).getText().split("/")[0];
                json.put("price", price);
                datalist.add(new String[]{title, data, String.valueOf(features), countries, String.valueOf(details), price});

                array.put(json);
            }
            writer.writeAll(datalist);
            writer.close();

            System.out.println("Bell data extraction finished");

            System.out.println(array);
        } catch (Exception e) {
            System.out.println("Some error occurred while scraping Bell");
            System.out.println(e);
        }
        return array;
    }

    // params (WebDriver driver, WebDriverWait wait)
    // driver : driver object used to find elements from a page
    // wait : WebdriverWait object used to implement implicit or explicit wait

    public static JSONArray extract_virgin(WebDriver driver, WebDriverWait wait) throws IOException {
        //define a csv filepath and name of the file that will be used to store the data

        System.out.println("Virgin Mobile data extraction starting...");

        String filepath = "/home/shubh/Documents/virgin_output.csv";
        File file = new File(filepath);

        JSONArray array = new JSONArray();

        //open application and the homepage
        driver.get("https://www.virginplus.ca/en/home/index.html");

        // Take a screenshot and save it to a file
//        TakesScreenshot screenshot = (TakesScreenshot) driver;
//        File srcFile = screenshot.getScreenshotAs(OutputType.FILE); // Take screenshot
//
//        // Specify the destination file path
//        File destFile = new File("/home/shubh/Desktop/1.png");
//
//        // Use FileUtils to save the file to the specified location
//        FileUtils.copyFile(srcFile, destFile);

        //Initialize file writer object to initialize CSVWriter object that is used to write the scraped data into
        FileWriter filewriter = new FileWriter(file);
        CSVWriter writer = new CSVWriter(filewriter);

        writer.writeNext(new String[]{"Data", "Type", "Features", "Details", "Price"});

        System.out.println("Virgin Mobile data extraction started");

        try {
            //Locate the navbar mobile button to open a dropdown list
            WebElement mobile = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("ul[class='topNav'] li:nth-child(2) div[aria-controls='accss-mobile-dd-menu']")));
            mobile.click();

            // Select the plans option from the dropdown list
            WebElement plans = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[aria-label='Plans']")));
            plans.click();

            // data : Used to store array of string which has plan_fearures and plan_price
            List<String[]> data = new ArrayList<String[]>();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("ratebox-BYOP")));

            // Locates the main div element that covers the main scraping area
            List<WebElement> buckets = driver.findElements(By.cssSelector("div.slidenarrow > div"));

            // Each bucket has plan-containers that has multiple plan options
            for (WebElement bucket : buckets) {
                JSONObject json = new JSONObject();
                List<WebElement> plan_container = bucket.findElements(By.cssSelector("div.planList > plan-container"));
//            System.out.println(plan_container.size());

                // Each plan-container is used to extract and individual plan and scrape features and price for each plan
                for (WebElement plan : plan_container) {
                    List<WebElement> plan_info = plan.findElements(By.cssSelector("div:nth-child(1) > div.planInner.tb > div"));

                    String heading = "", featureList = "";
                    for (WebElement info : plan_info) {
//                    .planid-63-03147-dtt > div:nth-child(1) > div:nth-child(1) > div:nth-child(1) > div:nth-child(1) > div:nth-child(1)
                        heading = info.findElement(By.cssSelector("div:nth-child(1) > div:nth-child(1)")).getText();
                        Pattern p = Pattern.compile("\\d+(GB|MB)");
                        Matcher m = p.matcher(heading);
                        if (m.find()) {
                            json.put("heading", heading.substring(m.start(), m.end()));
                        }
                        featureList = info.findElement(By.cssSelector("div:nth-child(1) > div:nth-child(2)")).getText();
                        json.put("features", featureList);
                    }
                    // extract features and price and convert them to string
                    String price = plan_info.get(1).getText();
                    json.put("price", price);

                    List<String> csv_data = new ArrayList<String>();

                    csv_data.add(heading);
                    csv_data.add(featureList);
                    csv_data.add(price);
                    
                    // convert array list to String[] since CSVWriter accepts String[]
                    String[] attr = new String[csv_data.size()];
                    attr = csv_data.toArray(attr);

                    // add each plan information to data variable
                    data.add(attr);
                }
                array.put(json);
            }
            // write all the extracted data at once to the csv file and then close the object
            writer.writeAll(data);
            writer.close();

            System.out.println("Virgin Mobile data extraction finished");

            System.out.println(array);
        } catch (Exception e) {
            System.out.println("Some error occured while scraping virgin plus");
            System.out.println(e);
        }
        return array;
    }

    public static JSONArray extract_rogers(WebDriver driver, WebDriverWait wait) throws IOException {

        System.out.println("Rogers data extraction starting...");

        String filepath = "/home/shubh/Documents/rogers_output.csv";
        File file = new File(filepath);

        JSONArray array = new JSONArray();

        driver.get("https://www.rogers.com/plans?icid=R_WIR_CMH_6WMCMZ");
        
        FileWriter filewriter = new FileWriter(file);
        CSVWriter writer = new CSVWriter(filewriter);

        writer.writeNext(new String[]{"Data", "Price", "Features", "Amount"});

        System.out.println("Rogers data extraction started");

        try {
            wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#ds-tabs-0-tab-1"))).click();
            List<WebElement> containers = driver.findElements(By.cssSelector("#mainlines-byod > div:nth-child(1) > div:nth-child(1) > div"));

            List<String[]> finaldata = new ArrayList<>();

            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            for (WebElement container : containers) {
                List<String> data = new ArrayList<>();
                JSONObject json = new JSONObject();
                String title = container.findElement(By.cssSelector("dsa-vertical-tile:nth-child(1) > ds-tile:nth-child(1) > div:nth-child(1) > div:nth-child(1) > div:nth-child(2) > div:nth-child(1) > p:nth-child(1)")).getText();
                Pattern p = Pattern.compile("[4-5]G");
                Matcher m = p.matcher(title);
                if (m.find()) {
                    json.put("title", title.substring(m.start(), m.end()));
                }
                data.add(title);

                String price = container.findElement(By.cssSelector("dsa-vertical-tile:nth-child(1) > ds-tile:nth-child(1) > div:nth-child(1) > div:nth-child(1) > div:nth-child(2) > div:nth-child(1) > div:nth-child(2) > dsa-price:nth-child(1) > div:nth-child(1) > ds-price:nth-child(1) > div:nth-child(1) > span:nth-child(1)")).getText();
                p = Pattern.compile("\\$\\d+.\\d+");
                m = p.matcher(price);
                if (m.find()) {
                    json.put("price", price.substring(m.start(), m.end()));
                }
                data.add(price);

                List<WebElement> featureElements = container.findElements(By.cssSelector("dsa-vertical-tile:nth-child(1) > ds-tile:nth-child(1) > div:nth-child(1) > div:nth-child(1) > div:nth-child(2) > div:nth-child(1) > ul:nth-child(3) > li:nth-child(1) > p:nth-child(2) > ul:nth-child(1) > li"));

                ArrayList<String> featureText = new ArrayList<>();
                for (WebElement feature : featureElements) {
                    featureText.add(feature.getText());
                }
                json.put("features", featureText);
                data.add(String.valueOf(featureText));

                try {
                    List<WebElement> detailsElements = container.findElements(By.cssSelector("dsa-vertical-tile:nth-child(1) > ds-tile:nth-child(1) > div:nth-child(1) > div:nth-child(1) > div:nth-child(2) > div:nth-child(1) > ul:nth-child(3) > li:nth-child(2) > p:nth-child(2) > ul:nth-child(1) > li"));

                    ArrayList<String> detailText = new ArrayList<>();
                    for (WebElement detail : detailsElements) {
                        featureText.add(detail.getText());
                    }
                    json.put("details", detailText);
                    data.add(String.valueOf(detailText));
                } catch (NoSuchElementException e) {
                    System.out.println("Travel feature plans does not exist for plan : " + title);
                }
                String amount = featureText.get(1);
                p = Pattern.compile("\\d+ GB");
                m = p.matcher(price);
                if (m.find()) {
                    json.put("amount", amount.substring(m.start(), m.end()));
                }
                data.add(amount);

                String[] attr = new String[data.size()];
                attr = data.toArray(attr);

                finaldata.add(attr);
                array.put(json);
            }

            writer.writeAll(finaldata);
            writer.close();

            System.out.println("Rogers data extraction finished");

            System.out.println(array);
        } catch (Exception e) {
            System.out.println("Some error occurred while scraping rogers");
            System.out.println(e);
        }
        return array;
    }

    public static JSONArray extract_freedom(WebDriver driver, WebDriverWait wait) throws IOException {
        System.out.println("Freedom data extraction starting...");

        String filepath = "/home/shubh/Documents/freedom_output.csv";
        File file = new File(filepath);

        JSONArray array = new JSONArray();

        driver.get("https://shop.freedommobile.ca/en-CA/plans");

        FileWriter filewriter = new FileWriter(file);
        CSVWriter writer = new CSVWriter(filewriter);

        writer.writeNext(new String[]{"Countries", "DataAmount", "Price", "Features"});

        System.out.println("Freedom data extraction started");

        List<WebElement> containers = driver.findElements(By.cssSelector("ul.duCQMX > li"));
        List<String[]> finaldata = new ArrayList<>();

        for(WebElement container: containers) {
            JSONObject json = new JSONObject();
            List<String> data = new ArrayList<>();
            String countries = container.findElement(By.cssSelector("div:nth-child(1) > div:nth-child(2) > div:nth-child(1) > div:nth-child(1) > div:nth-child(1) > div:nth-child(1) > p:nth-child(1) > span:nth-child(1)")).getText();
            json.put("countries", countries);
            data.add(countries);
            String dataamount = container.findElement(By.cssSelector("div:nth-child(1) > div:nth-child(2) > div:nth-child(1) > div:nth-child(1) > div:nth-child(1) > div:nth-child(2) > h3:nth-child(1) > span:nth-child(1)")).getText();
            json.put("dataamount", dataamount);
            data.add(dataamount);
            String price = container.findElement(By.cssSelector("div:nth-child(1) > div:nth-child(2) > div:nth-child(1) > div:nth-child(1) > div:nth-child(2) > div:nth-child(1) > div:nth-child(1) > div:nth-child(1) > span:nth-child(1)")).getText() + container.findElement(By.cssSelector("div:nth-child(1) > div:nth-child(2) > div:nth-child(1) > div:nth-child(1) > div:nth-child(2) > div:nth-child(1) > div:nth-child(1) > div:nth-child(2) > span:nth-child(1)")).getText();
            json.put("price", price);
            data.add(price);

            List<WebElement> featureList = container.findElements(By.cssSelector("div:nth-child(1) > div:nth-child(2) > div:nth-child(2) > div:nth-child(1) > div:nth-child(1) > div:nth-child(1) > div:nth-child(1) > div:nth-child(1) > div:nth-child(1) > div:nth-child(1) > div[data-testid='data-talk-text-description']"));
            List<String> features = new ArrayList<>();
            for(WebElement feature: featureList) {
                features.add(feature.findElement(By.cssSelector("p:nth-child(2) > p:nth-child(1)")).getText());
            }
            data.add(String.valueOf(features));

            String[] attr = new String[data.size()];
            attr = data.toArray(attr);

            finaldata.add(attr);

            json.put("features", features);
            array.put(json);
        }

        writer.writeAll(finaldata);
        writer.close();

        return array;
    }
}