
Pod::Spec.new do |s|
  s.name         = "RNCipher"
  s.version      = "1.0.0"
  s.summary      = "RNCipher"
  s.description  = <<-DESC
                  RNCipher
                   DESC
  s.homepage     = ""
  s.license      = "MIT"
  # s.license      = { :type => "MIT", :file => "FILE_LICENSE" }
  s.author             = { "author" => "author@domain.cn" }
  s.platform     = :ios, "7.0"
  s.source       = { :git => "https://github.com/author/RNCipher.git", :tag => "master" }
  s.source_files  = "RNCipher/**/*.{h,m}"
  s.requires_arc = true


  s.dependency "React"
  #s.dependency "others"

end

  