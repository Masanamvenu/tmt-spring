package com.tmt.automation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

@RestController
@RequestMapping("/api/pom")
public class PomController {

    private static final String POM_PATH = "pom.xml"; // relative to project root

    /**
     * Update the version of a dependency (selenium-java or testng) in pom.xml.
     * Example: POST /api/pom/update-dependency-version?artifactId=selenium-java&version=4.10.0
     */
    @PostMapping("/update-dependency-version")
    public ResponseEntity<String> updateDependencyVersion(
            @RequestParam String artifactId,
            @RequestParam String version) {
        try {
            File pomFile = new File(POM_PATH);
            if (!pomFile.exists()) {
                return ResponseEntity.badRequest().body("pom.xml not found.");
            }

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setIgnoringElementContentWhitespace(true);
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(pomFile);
            doc.getDocumentElement().normalize();

            NodeList dependencies = doc.getElementsByTagName("dependency");
            boolean updated = false;

            for (int i = 0; i < dependencies.getLength(); i++) {
                Node depNode = dependencies.item(i);
                if (depNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element depElement = (Element) depNode;
                    String depArtifactId = getTagValue("artifactId", depElement);

                    // Only update if artifactId matches selenium-java or testng
                    if (artifactId.equals(depArtifactId)) {
                        NodeList children = depElement.getElementsByTagName("version");
                        if (children.getLength() > 0) {
                            children.item(0).setTextContent(version);
                            updated = true;
                        } else {
                            Element versionElement = doc.createElement("version");
                            versionElement.setTextContent(version);
                            depElement.appendChild(versionElement);
                            updated = true;
                        }
                        break;
                    }
                }
            }

            if (!updated) {
                return ResponseEntity.badRequest().body("Dependency with artifactId '" + artifactId + "' not found in pom.xml.");
            }

            // Write changes back to pom.xml with minimal whitespace
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(pomFile);
            transformer.transform(source, result);

            // Rebuild the project using Maven
            String buildOutput = runMavenBuild();

            return ResponseEntity.ok("Dependency '" + artifactId + "' version updated to " + version + ".\n\nMaven Build Output:\n" + buildOutput);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating pom.xml: " + e.getMessage());
        }
    }

    private String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag);
        if (nodeList != null && nodeList.getLength() > 0) {
            Node node = nodeList.item(0);
            return node.getTextContent();
        }
        return null;
    }

    // Run 'mvn clean install' and return the output
    private String runMavenBuild() {
        StringBuilder output = new StringBuilder();
        try {
            ProcessBuilder builder = new ProcessBuilder();
            String os = System.getProperty("os.name").toLowerCase();
            File mvnwCmd = new File("mvnw.cmd");
            File mvnw = new File("mvnw");
            if (os.contains("win") && mvnwCmd.exists()) {
                builder.command("cmd.exe", "/c", mvnwCmd.getAbsolutePath(), "clean", "install");
            } else if (mvnw.exists()) {
                builder.command(mvnw.getAbsolutePath(), "clean", "install");
            } else {
                builder.command("mvn", "clean", "install");
            }
            builder.redirectErrorStream(true);
            builder.directory(new File(".")); // project root

            Process process = builder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            process.waitFor();
        } catch (Exception ex) {
            output.append("Error running Maven build: ").append(ex.getMessage());
        }
        return output.toString();
    }
}