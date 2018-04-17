X = 1:6;
    % Set the tick locations and remove the labels 
    set(gca,'XTick',1:6,'XTickLabel',''); 
    % Define the labels 
    lab = [{'Precision(DS1)'};{'Recall(DS1)'};{'F1-Score(DS1)'};{'Precision(DS2)'};{'Recall(DS2)'};{'F1-Score(DS2)'}];
    % Estimate the location of the labels based on the position of the xlabel 
    set(gca,'Position',[.05 .05 .95 .9])
    hx = get(gca,'XLabel');  % Handle to xlabel 
    set(hx,'Units','data'); 
    pos = get(hx,'Position'); 
    y = pos(2); 
    % Place the new labels 
    for i = 1:size(lab,1) 
        t(i) = text(X(i),y,lab(i,:)); 
    end 
    set(t,'Rotation',-30,'HorizontalAlignment','left') 