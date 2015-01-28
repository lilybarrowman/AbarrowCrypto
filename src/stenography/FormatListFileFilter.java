package stenography;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class FormatListFileFilter extends FileFilter {

  
  private String description;
  private String[] formats;

  
  public FormatListFileFilter(String[] acceptedFormats, String about) {
    formats = acceptedFormats;
    description = about;
  }
  
  @Override
  public boolean accept(File file) {
    if (!file.isFile()) {
      return true;
    }
    String name = file.getName();
    int dotIndex = name.lastIndexOf('.');
    if (dotIndex < 0) {
      return false;
    }
    String type = name.substring(dotIndex + 1).toLowerCase();
    
    
    for (int i = 0; i < formats.length;i++) {
      if(type.equals(formats[i])){
        return true;
      }
    }
    return false;
  }

  @Override
  public String getDescription() {
    return description;
  }

}
