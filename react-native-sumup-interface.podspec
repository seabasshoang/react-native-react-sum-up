require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "react-native-sumup-wrapper"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.description  = <<-DESC
                  react-native-sumup-wrapper
                   DESC
  s.homepage     = "https://github.com/seabasshoang/react-native-react-sum-up"
  s.license      = "MIT"
  s.authors      = "AMI3GO Ltd"
  s.platforms    = { :ios => "9.0", :tvos => "10.0" }
  s.source       = { :git => "https://github.com/seabasshoang/react-native-react-sum-up.git", :tag => "#{s.version}" }

  s.source_files = "ios/**/*.{h,m,swift}"
  s.requires_arc = true

  s.dependency "React"
  s.dependency 'SumUpSDK'
	
  # s.dependency "..."
end

