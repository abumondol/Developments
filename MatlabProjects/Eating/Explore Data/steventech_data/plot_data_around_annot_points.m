% subject = 0;
% session = 1;
% annots = csvread(strcat('K:\ASM\projects\eating\eating_steventech\data\text\lab_annots_0',num2str(subject),'_',num2str(session),'.csv'));
% right = csvread(strcat('K:\ASM\projects\eating\eating_steventech\data\text\lab_right_0',num2str(subject),'_',num2str(session),'.csv'));
% left = csvread(strcat('K:\ASM\projects\eating\eating_steventech\data\text\lab_left_0',num2str(subject),'_',num2str(session),'.csv'));
% glass = csvread(strcat('K:\ASM\projects\eating\eating_steventech\data\text\lab_glass_0',num2str(subject),'_',num2str(session),'.csv'));
% save('data', 'annots', 'right', 'left', 'glass');
% return

annots = annots(annots(:,3)==3 | annots(:,3)==4 | annots(:,3)==21 | annots(:,3)==22, :);
annots = [0, 0, 0; annots];

a = [];
for i=3:length(annots)
    if annots(i,end) == 3 || annots(i,end) == 4
        if annots(i-1,end) == 21 || annots(i-2,end) == 21
            a = [a; annots(i, :), 1];            
        end
        
        if annots(i-1,end) == 22 || annots(i-2,end) == 22
            a = [a; annots(i, :), 2];
        end
        
    end
end

a = a(a(:,end-1) == 3, :);
a = a(a(:,end) == 1, :);
offset = 10;
d = glass;
rate = length(d)/(d(end,1)-d(1,1))
d(:, 2:4) = mysmooth(d(:,2:4), 0.9);

for i=1:length(a)    
    x = d( d(:,1)>=a(i,1)-offset & d(:,1)<a(i,1)+offset, :); 
    
    close all;
    figure('units','normalized','outerposition',[0 0 1 1]);
    plot(x(:, 1), x(:, 2:4));
    legend('X','Y','Z');
    ttl = strcat('Type:', num2str(a(i, 3)));            
    title(ttl);
    grid on
    waitforbuttonpress
end

return
    
    
    %figure

    d = d1(d1(:,1) >= s-offset & d1(:,1) <= s+offset, :);            
    subplot(2,1,1)       % add first plot in 2 x 1 grid

    ttl = strcat('sub:', num2str(subject), ', annot no:', num2str(i), ', time:', num2str(annots(i,1)), ', label:', num2str(label) );            
    x = d(:,1); % timestamps
    x = x - s;
    y = d(:,2:4);        
    plot(x, y);
    
    title(ttl);
    hold on
    grid on
    plot([x(1);x(end)], [0; 0], 'k'); %plot zero line                        
    scatter(0, 0, 'rd'); % annotated mark point        

    d = df(d1(:,1) >= s-offset & d1(:,1) <= s+offset, :);   
    d = [mysmooth(d, 0.5)];
    subplot(2,1,2)       % add second plot in 2 x 1 grid            
    %x = d(:, 1); % timestamps
    %x = x - s;
    y = d;
    %y = fft(y);        
    %y(10:end-9) = 0;
    %y = ifft(y);       
%         y = d(:,2:4); %x, y, z axes val
%         mag = sqrt(sum(y.*y, 2));
%         y = [y, mag];
    plot(x, y);
%        legend('X','Y','Z', 'M');                        
    hold on
    
    plot([x(1);x(end)], [0; 0], 'k'); %plot zero line                        
    scatter(0, 0, 'rd'); % annotated mark point          


    
