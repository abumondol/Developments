function result = read_raw_data(names)

fprintf('Reading raw sign data ... \n');
signatures = [];
result = true;
k=1;
for i = 1:length(names)    
    folder = strcat('sign_data/',names(i));    
    files = dir(char(folder));   
    
    serial = 1;
    for j=3:length(files)  % first two files are . and ..        
        file_name = files(j).name;        
        file_path = strcat(folder,'/',file_name);       
        fprintf('Reading %d : %s \n', k, char(file_path));
        st = strsplit(file_name,'_');
        
        if strcmpi(names(i),'Speed') == false && strcmpi(st{2},names(i)) == false
            display('Name doesnt match');
            names(i)
            file_name
            result = false;
            return;
        end
        
        signatures(k).serial = serial;
        signatures(k).subject_index = i;  
        signatures(k).subject_name = st{2};
        signatures(k).file_name = file_name;
        signatures(k).raw_data = csvread(char(file_path));        
        signatures(k).processed = 0;        
        
        
        serial = serial + 1;
        k = k + 1;        
    end
    
end

save('signatures','signatures');

fprintf('Reading raw sign data done\n\n');