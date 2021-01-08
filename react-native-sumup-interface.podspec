require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "react-native-sumup-interface"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.description  = <<-DESC
                  react-native-sumup-interface
                   DESC
  s.homepage     = "https://github.com/AMI3GOLtd/react-native-react-sum-up"
  s.license      = "MIT"
  s.authors      = "AMI3GO Ltd"
  s.platforms    = { :ios => "9.0", :tvos => "10.0" }
  s.source       = { :git => "https://github.com/AMI3GOLtd/react-native-react-sum-up.git", :tag => "#{s.version}" }

  s.source_files = "ios/**/*.{h,m,swift}"
  s.requires_arc = true

  s.dependency "React"
  s.dependency 'SumUpSDK'
	
  # s.dependency "..."
end

