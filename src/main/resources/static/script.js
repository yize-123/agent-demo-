class AIChatApp {
    constructor() {
        this.currentConversationId = null;
        this.conversations = [];
        this.apiBaseUrl = '/api/agent';
        this.init();
    }

    init() {
        this.bindEvents();
        this.loadConversations();
        this.loadTools();
        this.initTheme();
    }

    bindEvents() {
        const themeToggle = document.getElementById('themeToggle');
        const sendBtn = document.getElementById('sendBtn');
        const messageInput = document.getElementById('messageInput');
        const clearChatBtn = document.getElementById('clearChatBtn');
        const newConversationBtn = document.getElementById('newConversationBtn');
        const startDemoBtn = document.getElementById('startDemoBtn');
        const viewDocsBtn = document.getElementById('viewDocsBtn');
        const quickBtns = document.querySelectorAll('.quick-btn');
        const navLinks = document.querySelectorAll('.nav-link');

        themeToggle.addEventListener('click', () => this.toggleTheme());
        
        sendBtn.addEventListener('click', () => this.sendMessage());
        
        messageInput.addEventListener('input', () => this.handleInputChange());
        
        messageInput.addEventListener('keydown', (e) => {
            if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                this.sendMessage();
            }
        });

        clearChatBtn.addEventListener('click', () => this.clearChat());
        
        newConversationBtn.addEventListener('click', () => this.createNewConversation());
        
        startDemoBtn.addEventListener('click', () => this.scrollToSection('demo'));
        
        viewDocsBtn.addEventListener('click', () => window.open('README.md', '_blank'));

        quickBtns.forEach(btn => {
            btn.addEventListener('click', () => {
                const prompt = btn.dataset.prompt;
                messageInput.value = prompt;
                this.handleInputChange();
                this.sendMessage();
            });
        });

        navLinks.forEach(link => {
            link.addEventListener('click', (e) => {
                e.preventDefault();
                const section = link.getAttribute('href').substring(1);
                this.scrollToSection(section);
                
                navLinks.forEach(l => l.classList.remove('active'));
                link.classList.add('active');
            });
        });

        window.addEventListener('scroll', () => this.handleScroll());
    }

    initTheme() {
        const savedTheme = localStorage.getItem('theme') || 'light';
        document.documentElement.setAttribute('data-theme', savedTheme);
    }

    toggleTheme() {
        const currentTheme = document.documentElement.getAttribute('data-theme');
        const newTheme = currentTheme === 'dark' ? 'light' : 'dark';
        document.documentElement.setAttribute('data-theme', newTheme);
        localStorage.setItem('theme', newTheme);
    }

    handleScroll() {
        const sections = document.querySelectorAll('section');
        const navLinks = document.querySelectorAll('.nav-link');

        let currentSection = '';
        sections.forEach(section => {
            const sectionTop = section.offsetTop;
            if (window.scrollY >= sectionTop - 100) {
                currentSection = section.getAttribute('id');
            }
        });

        navLinks.forEach(link => {
            link.classList.remove('active');
            if (link.getAttribute('href') === `#${currentSection}`) {
                link.classList.add('active');
            }
        });
    }

    scrollToSection(sectionId) {
        const section = document.getElementById(sectionId);
        if (section) {
            section.scrollIntoView({ behavior: 'smooth' });
        }
    }

    handleInputChange() {
        const messageInput = document.getElementById('messageInput');
        const sendBtn = document.getElementById('sendBtn');
        const charCount = document.querySelector('.char-count');

        const value = messageInput.value.trim();
        sendBtn.disabled = value.length === 0;
        charCount.textContent = `${value.length} / 2000`;

        this.autoResizeTextarea(messageInput);
    }

    autoResizeTextarea(textarea) {
        textarea.style.height = 'auto';
        textarea.style.height = Math.min(textarea.scrollHeight, 150) + 'px';
    }

    async sendMessage() {
        const messageInput = document.getElementById('messageInput');
        const message = messageInput.value.trim();

        if (!message) return;

        this.removeWelcomeMessage();
        this.addUserMessage(message);
        messageInput.value = '';
        this.handleInputChange();

        const loadingMessage = this.addLoadingMessage();

        try {
            const response = await this.callAgentAPI(message);
            this.removeMessage(loadingMessage);
            this.addAssistantMessage(response);
            
            if (response.conversationId && response.conversationId !== this.currentConversationId) {
                this.currentConversationId = response.conversationId;
                this.loadConversations();
            }
        } catch (error) {
            this.removeMessage(loadingMessage);
            this.addErrorMessage(error.message);
        }
    }

    async callAgentAPI(message) {
        const response = await fetch(`${this.apiBaseUrl}/chat`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                conversationId: this.currentConversationId,
                message: message
            })
        });

        if (!response.ok) {
            throw new Error('API请求失败');
        }

        return await response.json();
    }

    addUserMessage(content) {
        const messagesContainer = document.getElementById('chatMessages');
        const messageDiv = this.createMessageElement('user', content);
        messagesContainer.appendChild(messageDiv);
        this.scrollToBottom(messagesContainer);
    }

    addAssistantMessage(response) {
        const messagesContainer = document.getElementById('chatMessages');
        
        const messageDiv = document.createElement('div');
        messageDiv.className = 'chat-message assistant';
        messageDiv.innerHTML = `
            <div class="message-avatar">AI</div>
            <div class="message-content">
                <div class="assistant-response">${this.formatResponse(response)}</div>
                ${response.thoughts && response.thoughts.length > 0 ? this.createThoughtsSection(response.thoughts) : ''}
            </div>
        `;
        
        messagesContainer.appendChild(messageDiv);
        this.scrollToBottom(messagesContainer);
    }

    formatResponse(response) {
        let html = `<div class="response-text">${this.escapeHtml(response.answer)}</div>`;
        
        if (response.toolsUsed && response.toolsUsed.length > 0) {
            html += `
                <div class="tools-used">
                    <span class="tools-label">使用工具:</span>
                    ${response.toolsUsed.map(tool => `<span class="tool-badge">${tool}</span>`).join('')}
                </div>
            `;
        }
        
        if (response.responseTimeMs) {
            html += `<div class="response-time">响应时间: ${response.responseTimeMs}ms</div>`;
        }
        
        return html;
    }

    createThoughtsSection(thoughts) {
        return `
            <div class="thoughts-section">
                <div class="thoughts-header" onclick="this.parentElement.classList.toggle('expanded')">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                        <polyline points="6 9 12 15 18 9"/>
                    </svg>
                    <span>思考过程 (${thoughts.length}步)</span>
                </div>
                <div class="thoughts-list">
                    ${thoughts.map((thought, index) => `
                        <div class="thought-item">
                            <span class="thought-number">${index + 1}</span>
                            <span class="thought-text">${this.escapeHtml(thought)}</span>
                        </div>
                    `).join('')}
                </div>
            </div>
        `;
    }

    addLoadingMessage() {
        const messagesContainer = document.getElementById('chatMessages');
        const loadingId = 'loading-' + Date.now();
        
        const loadingDiv = document.createElement('div');
        loadingDiv.className = 'chat-message assistant loading';
        loadingDiv.id = loadingId;
        loadingDiv.innerHTML = `
            <div class="message-avatar">AI</div>
            <div class="message-content">
                <div class="loading-dots">
                    <span></span>
                    <span></span>
                    <span></span>
                </div>
            </div>
        `;
        
        messagesContainer.appendChild(loadingDiv);
        this.scrollToBottom(messagesContainer);
        
        return loadingId;
    }

    addErrorMessage(error) {
        const messagesContainer = document.getElementById('chatMessages');
        const errorDiv = this.createMessageElement('error', `错误: ${error}`);
        messagesContainer.appendChild(errorDiv);
        this.scrollToBottom(messagesContainer);
    }

    createMessageElement(type, content) {
        const div = document.createElement('div');
        div.className = `chat-message ${type}`;
        
        const avatar = type === 'user' ? 'U' : type === 'error' ? '!' : 'AI';
        
        div.innerHTML = `
            <div class="message-avatar">${avatar}</div>
            <div class="message-content">${this.escapeHtml(content)}</div>
        `;
        
        return div;
    }

    removeMessage(messageId) {
        const message = document.getElementById(messageId);
        if (message) {
            message.remove();
        }
    }

    removeWelcomeMessage() {
        const welcomeMessage = document.querySelector('.welcome-message');
        if (welcomeMessage) {
            welcomeMessage.remove();
        }
    }

    scrollToBottom(container) {
        container.scrollTop = container.scrollHeight;
    }

    escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }

    clearChat() {
        if (confirm('确定要清空当前对话吗？')) {
            const messagesContainer = document.getElementById('chatMessages');
            messagesContainer.innerHTML = '';
            this.currentConversationId = null;
            this.loadConversations();
        }
    }

    createNewConversation() {
        this.currentConversationId = null;
        const messagesContainer = document.getElementById('chatMessages');
        messagesContainer.innerHTML = this.getWelcomeMessageHTML();
        this.loadConversations();
    }

    async loadConversations() {
        try {
            const response = await fetch(`${this.apiBaseUrl}/conversations`);
            const data = await response.json();
            
            if (data.conversation_ids && data.conversation_ids.length > 0) {
                this.renderConversations(data.conversation_ids);
            }
        } catch (error) {
            console.error('加载对话列表失败:', error);
        }
    }

    renderConversations(conversationIds) {
        const conversationList = document.getElementById('conversationList');
        
        let html = `
            <div class="conversation-item ${!this.currentConversationId ? 'active' : ''}" 
                 onclick="app.switchConversation(null)">
                <span class="conversation-title">新对话</span>
                <span class="conversation-time">刚刚</span>
            </div>
        `;
        
        conversationIds.forEach((id, index) => {
            const isActive = id === this.currentConversationId;
            html += `
                <div class="conversation-item ${isActive ? 'active' : ''}" 
                     onclick="app.switchConversation('${id}')">
                    <span class="conversation-title">对话 ${index + 1}</span>
                    <span class="conversation-time">历史</span>
                </div>
            `;
        });
        
        conversationList.innerHTML = html;
    }

    switchConversation(conversationId) {
        this.currentConversationId = conversationId;
        this.loadConversations();
        
        if (conversationId) {
            this.removeWelcomeMessage();
        } else {
            this.createNewConversation();
        }
    }

    async loadTools() {
        try {
            const response = await fetch(`${this.apiBaseUrl}/tools`);
            const data = await response.json();
            
            if (data.tools && data.tools.length > 0) {
                this.renderTools(data.tools);
            }
        } catch (error) {
            console.error('加载工具列表失败:', error);
        }
    }

    renderTools(tools) {
        const toolsList = document.getElementById('toolsList');
        
        let html = '';
        tools.forEach(tool => {
            const [name, description] = tool.split(': ');
            html += `
                <div class="tool-item">
                    <span class="tool-name" title="${description}">${name}</span>
                    <span class="tool-status">可用</span>
                </div>
            `;
        });
        
        toolsList.innerHTML = html;
    }

    getWelcomeMessageHTML() {
        return `
            <div class="welcome-message">
                <div class="welcome-icon">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                        <circle cx="12" cy="12" r="10"/>
                        <path d="M8 14s1.5 2 4 2 4-2 4-2"/>
                        <line x1="9" y1="9" x2="9.01" y2="9"/>
                        <line x1="15" y1="9" x2="15.01" y2="9"/>
                    </svg>
                </div>
                <h3>欢迎使用 AI Agent</h3>
                <p>我可以帮您：</p>
                <ul>
                    <li>搜索网络信息</li>
                    <li>读取和分析文件</li>
                    <li>管理待办事项</li>
                    <li>浏览目录结构</li>
                    <li>抓取网页内容</li>
                </ul>
            </div>
        `;
    }
}

const app = new AIChatApp();

document.addEventListener('DOMContentLoaded', () => {
    const style = document.createElement('style');
    style.textContent = `
        .assistant-response {
            line-height: 1.6;
            margin-bottom: 1rem;
        }
        
        .tools-used {
            display: flex;
            align-items: center;
            gap: 0.5rem;
            flex-wrap: wrap;
            margin-top: 0.5rem;
        }
        
        .tools-label {
            font-size: 0.875rem;
            color: var(--text-secondary);
        }
        
        .tool-badge {
            padding: 0.25rem 0.75rem;
            background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
            border-radius: var(--radius-md);
            font-size: 0.75rem;
            font-weight: 500;
            color: white;
        }
        
        .response-time {
            font-size: 0.75rem;
            color: var(--text-tertiary);
            margin-top: 0.5rem;
        }
        
        .thoughts-section {
            margin-top: 1rem;
            border: 1px solid var(--border-color);
            border-radius: var(--radius-md);
            overflow: hidden;
        }
        
        .thoughts-header {
            display: flex;
            align-items: center;
            gap: 0.5rem;
            padding: 0.75rem 1rem;
            background: var(--bg-tertiary);
            cursor: pointer;
            font-size: 0.875rem;
            font-weight: 500;
            color: var(--text-secondary);
            transition: background-color var(--transition-fast);
        }
        
        .thoughts-header:hover {
            background: var(--border-color);
        }
        
        .thoughts-header svg {
            width: 16px;
            height: 16px;
            transition: transform var(--transition-fast);
        }
        
        .thoughts-section.expanded .thoughts-header svg {
            transform: rotate(180deg);
        }
        
        .thoughts-list {
            display: none;
            padding: 1rem;
        }
        
        .thoughts-section.expanded .thoughts-list {
            display: block;
        }
        
        .thought-item {
            display: flex;
            gap: 0.75rem;
            margin-bottom: 0.75rem;
        }
        
        .thought-item:last-child {
            margin-bottom: 0;
        }
        
        .thought-number {
            width: 24px;
            height: 24px;
            background: var(--primary-color);
            color: white;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 0.75rem;
            font-weight: 600;
            flex-shrink: 0;
        }
        
        .thought-text {
            flex: 1;
            font-size: 0.875rem;
            color: var(--text-secondary);
            line-height: 1.5;
        }
        
        .loading-dots {
            display: flex;
            gap: 0.25rem;
            padding: 1rem;
        }
        
        .loading-dots span {
            width: 8px;
            height: 8px;
            background: var(--primary-color);
            border-radius: 50%;
            animation: bounce 1.4s infinite ease-in-out both;
        }
        
        .loading-dots span:nth-child(1) {
            animation-delay: -0.32s;
        }
        
        .loading-dots span:nth-child(2) {
            animation-delay: -0.16s;
        }
        
        @keyframes bounce {
            0%, 80%, 100% {
                transform: scale(0);
            }
            40% {
                transform: scale(1);
            }
        }
        
        .chat-message.error .message-content {
            background: #fee2e2;
            color: #dc2626;
        }
        
        [data-theme="dark"] .chat-message.error .message-content {
            background: #450a0a;
            color: #fca5a5;
        }
    `;
    document.head.appendChild(style);
});