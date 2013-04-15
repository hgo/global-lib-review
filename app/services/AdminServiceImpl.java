package services;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import models.Library;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.BeanAccess;

import play.Logger;
import play.libs.IO;

class AdminServiceImpl implements AdminService {

    @Override
    public void importFromFile(File file) {
        try {
            Yaml yaml = new Yaml();
            yaml.setBeanAccess(BeanAccess.FIELD);
            Object loaded = yaml.load(IO.readContentAsString(file));
            ArrayList list = (ArrayList) loaded;
            for (Object object : list) {
                boolean found = false;
                LinkedHashMap linkedHashMap = (LinkedHashMap) object;
                Library library = new Library();
                // library.versions = new ArrayList<Version>();
                for (Object libFieldEntry : linkedHashMap.entrySet()) {
                    Map.Entry entry = (Entry) libFieldEntry;
                    String fieldName = (String) entry.getKey();
                    if (fieldName.equals("name")) {
                        if (Library.find("byName", entry.getValue()).first() != null) {
                            Logger.info(entry.getValue().toString() + " is already in db");
                            found = true;
                            break;
                        } else {
                            library.name = String.valueOf(entry.getValue());
                        }
                    } else if (fieldName.equals("link")) {
                        library.link = String.valueOf(entry.getValue());
                    }
                    else if (fieldName.equals("active")) {
                        library.active = Integer.valueOf(entry.getValue().toString()) == 1;
                    }
                    else if (fieldName.equals("description")) {
                        library.descMarkdown = entry.getValue().toString();
                    }
                    else if (fieldName.equals("language")) {
                        library.language = (entry.getValue() == null) ? "unknown" : entry.getValue().toString();
                    }
                }
                if (!found)
                    library.save();
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }
}
