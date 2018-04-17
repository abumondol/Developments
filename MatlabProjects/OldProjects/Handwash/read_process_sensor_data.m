src_dir= 'C:\Users\mm5gg\Box Sync\Data Sets\Hand wash\Dispenser with Beacon data\Test on Aug 29 2017\data';
files = dir(src_dir);
file_count = length(files)
data = [];
offsets = [-3, -3];

for i = 3:file_count    
    file_name = files(i).name;
    file_path = strcat(src_dir, '\' ,file_name)
    d = csvread(char(file_path));     
    %d = dlmread(char(file_path));     
    
    subj = 1; %samsung
    if ~isempty(strfind(file_name, 'asus15'))
        subj = 2;
    end
    
    sess = 1; %ir
    if ~isempty(strfind(file_name, 'button'))
        sess = 2;
    end

    
    start_time = d(1,1) + offsets(subj);
    d = d(2:end, :);
    if ~isempty(strfind(file_name, 'beacon'))
        b = d(:, [1, 2, 4]);
        bc = b(2:end,2)-b(1:end-1,2);
        bc = bc>0;
        bc = [0;bc];
        data(sess).subject(subj).beacon = [b, bc];        
    else
        accel = d(d(:,2)==1, [1, 4:6]);
        accel(:,1) = round(accel(:,1)/1e6);
        accel(:,1) = accel(:,1) - accel(1,1);
        accel(:,1) = accel(:,1) + start_time;
        data(sess).subject(subj).accel = accel;
        
    end
    
end    

mtime = 1503979200000; %date to milliseconds
file_path= 'C:\Users\mm5gg\Box Sync\Data Sets\Hand wash\Dispenser with Beacon data\Test on Aug 29 2017\ir_events.txt';
ir_events = csvread(file_path);
%file_path= 'C:\Users\mm5gg\Box Sync\Data Sets\Hand wash\Dispenser with Beacon data\Test on Aug 23 2017\button_events.txt';
%button_events = csvread(file_path);

t = mtime + 1000*(ir_events(:,4)*3600 + ir_events(:,5)*60 + ir_events(:,6));
data(1).events = [t, ir_events(:,end)];

%t = mtime + 1000*(button_events(:,4)*3600 + button_events(:,5)*60 + button_events(:,6));
%data(2).events = [t, button_events(:,end)];

save('data', 'data');