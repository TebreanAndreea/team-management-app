/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

@SpringBootApplication
@EntityScan(basePackages = {"commons", "server"})
public class Main {

    public static void main(String[] args) {
        generateServerPassword();
        SpringApplication.run(Main.class, args);
    }

    public static void generateServerPassword() {
        String result = "";
        Random random = new Random();
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        while (result.length() < 10) {
            result += characters.charAt(random.nextInt(characters.length()));
        }
        try {
            FileWriter writer = new FileWriter("server/src/adminPass.txt");
            writer.write(result);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            File file = new File("server/src/adminPass.txt");
            try {
                file.delete();
                file.createNewFile();

            } catch (IOException ex) {
                //Failed to create temp file.
            }
        }
    }
}