load('data_usc');
data = data_usc;
offset = 3;

for sub = 3:3    
    d1 = data(sub).accel(:, :);
    rate = length(d1)/(d1(end,1)-d1(1,1))
    d1(:,2:4) = smooth(d1(:,2:4), 0.90);        
    d2 = diff(d1(:, 2:4));       
    d2 = [d1(:,1), [d2; d2(end,:)]];
    d1(:,2:4) = smooth(d2(:,2:4), 0.90);       
    
    

    annots = data(sub).annotations;
    %annots = annots(annots(:,2)==102, :);            
    
    %d2(:,2:4) = d2(:, 2:4) - d1(:,2:4); %linear acceleration
    %d2(:,2:4) = smooth(d2(:,2:4), 0.95);                
    

    annot_count = size(annots, 1);
    for i = 1:annot_count
        s = annots(i,1);
        label = annots(i,2);            
        close all;
        %figure('units','normalized','outerposition',[0 0 1 1]);
        figure

        d = d1;
        d = d(d(:,1) >= s-offset & d(:,1) < s+offset, :);
        
        s = d(:,2);
        nsc = floor(length(s)/10);
        spectrogram(s, hamming(nsc), 'yaxis');
        %size(x)=
        waitforbuttonpress
    end
                
end


