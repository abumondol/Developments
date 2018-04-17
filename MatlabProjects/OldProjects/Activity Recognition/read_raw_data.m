function read_raw_data()

fprintf('Reading raw data ... \n');
data = [];
files = dir('data');

k = 1;
    
for j=3:length(files)  % first two files are . and ..
    file_name = files(j).name;        
    file_path = strcat('data/',file_name);
    data(k).raw_data = csvread(char(file_path));      
    data(k).file_name = file_name;  
    k = k + 1;
    
end

save('data','data');

fprintf('Reading raw sign data done\n');